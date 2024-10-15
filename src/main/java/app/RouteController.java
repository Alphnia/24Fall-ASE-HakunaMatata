package app;

import app.DatabaseOperation;

import java.util.List;
import java.util.Map;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.MongoCursor;

import org.springframework.boot.system.ApplicationTemp;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.bson.Document;
import org.hibernate.engine.internal.Collections;

@RestController
public class RouteController {

  private static final String API_URL = "https://routes.googleapis.com/directions/v2:computeRoutes";
  private static final String API_KEY = "AIzaSyDDe643HpTH5XXUBOZLNuJrcCFBdKM4k8Q"; // Your API key
  private static final String FIELD_MASK = "routes.legs.steps.transitDetails";
  
  @GetMapping("/")
  public String sayHello() {
    String origin = "28-30 Jackson Ave,Long Island City,NY 11101";
    String destination = "116th and Broadway, New York, NY 10027";
    RouteRequestGoogle routeRequest = new RouteRequestGoogle(origin, destination);
    Map<String, Object> entity = routeRequest.getRequestEntity();
    computeRoutes(entity);
    ResponseEntity<String> r = computeRoutes(entity);
    // System.out.println(r.getBody());
    retrieveRoute(origin,destination);
    // String"Retrieve response:"+ .getBody()
    // System.out.println("Hello, World!");
    ReadJSON j = new ReadJSON(r.getBody());
    
    System.out.println(j.getContent());
    return "Hello, World!";
  }

  @PostMapping("/computeRoutes")
  public ResponseEntity<String> computeRoutes(@RequestBody Map<String, Object> routeRequest) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");
    headers.set("X-Goog-Api-Key", API_KEY);
    headers.set("X-Goog-FieldMask", FIELD_MASK);
    
    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(routeRequest, headers);

    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<String> response = restTemplate.exchange(
      API_URL,
      HttpMethod.POST,
      requestEntity,
      String.class
    );

    return response;
  }
  
  /**
   * .
   *
   * @param origin   A {@code String} .
   *
   * @param destination A {@code String} .
   *
   * @return A {@code ResponseEntity} object containing either the requested
   *         information
   *         and an HTTP 200 response or, an appropriate message indicating the
   *         proper
   *         response.
   */
  @GetMapping(value = "/retrieveRoute", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> retrieveRoute(@RequestParam("origin") String origin,
    @RequestParam("destination") String destination) {
    try {
      boolean doesRouteExists;
      doesRouteExists = doesRouteExists(origin, destination).getStatusCode() == HttpStatus.OK;
      
      if (doesRouteExists) {
        DatabaseOperation DB = new DatabaseOperation(origin, destination);
        String document = DB.FindDocumentbyOriDes(origin, destination);
        System.out.println("Find document:" + document);
        return new ResponseEntity<>(document, HttpStatus.OK);
      }
      else {
        RouteRequestGoogle routeRequest = new RouteRequestGoogle(origin, destination);
        Map<String, Object> entity = routeRequest.getRequestEntity();
        computeRoutes(entity);
        // String origin = "28-30 Jackson Ave,Long Island City,NY 11101";
        // String destination = "116th and Broadway, New York, NY 10027";
        ResponseEntity<String> r = computeRoutes(entity);
        return new ResponseEntity<>("", HttpStatus.OK);
      }
    } catch (Exception e) {
      return handleException(e);
    }
  }


  /**
   * Returns the details of the specified route.
   *
   * @param origin A {@code String} representing the origin the user wishes
   *                 to inquire.
   *
   * @param destination A {@code String} representing the destination the user wishes
   *                 to inquire.
   * 
   * @return A {@code ResponseEntity} object containing either the details of the Route and
   *         an HTTP 200 response or, an appropriate message indicating the proper response.
   */
  @GetMapping(value = "/doesRouteExists", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> doesRouteExists(@RequestParam("origin") String origin,
    @RequestParam("destination") String destination) {
    try {
      DatabaseOperation DB = new DatabaseOperation(origin, destination);
      String document = DB.FindDocumentbyOriDes(origin, destination);
      if (document != null) {
        // System.out.println("This route exists:" + OriDes);
        // System.out.println(foundDocument.toJson());
        return new ResponseEntity<>(HttpStatus.OK);
      } else {
        System.out.println("No route Found:");
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      return handleException(e);
    }
  }

  /**
   * Returns the details of the specified route.
   * @param rawjson A {@code String} representing the rawjson from google map.
   * 
   * @param origin A {@code String} representing the origin the user wishes
   *                 to inquire.
   *
   * @param destination A {@code String} representing the destination the user wishes
   *                 to inquire.
   * 
   * @param stoplist A {@code List} representing the list of stops of the whole route.
   * 
   * @param annotatedlist A {@code List} representing the list of annotations of the route.
   * 
   * @return A {@code ResponseEntity} object containing either the details of the Route and
   *         an HTTP 200 response or, an appropriate message indicating the proper response.
   */
  @PatchMapping(value = "/createRoute", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> createRoute(
    @RequestParam("rawjson") String rawjson,
    @RequestParam("origin") String origin,
    @RequestParam("destination") String destination,
    @RequestParam("stoplist") List<Map<String, Object>> stoplist,
    @RequestParam("annotatedlist") List<Map<String, Object>> annotatedlist) {
    String connectionString = "mongodb+srv://team_public:Hakunamatata@cluster4156.287dv.mongodb.net/?retryWrites=true&w=majority&appName=Cluster4156";
    try (MongoClient mongoClient = MongoClients.create(connectionString)) {
      MongoDatabase database = mongoClient.getDatabase("Hkunamatata_DB"); 
      MongoCollection<Document> collection = database.getCollection("Route");
      String[] OriDes = new String[2];
      OriDes[0]=origin;
      OriDes[1]=destination;
      long count = collection.countDocuments();
      Document newDocument = new Document("RouteID", count-1)
                    .append("rawjson", rawjson)
                    .append("OriDes", OriDes)
                    .append("Stoplist", stoplist)
                    .append("Annotatedlist", annotatedlist);
      collection.insertOne(newDocument);
      try {
        collection.insertOne(newDocument);
        return new ResponseEntity<>(HttpStatus.OK);

      }catch (Exception e) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      return handleException(e);
    }
  }

  private ResponseEntity<?> handleException(Exception e) {
    System.out.println(e.toString());
    return new ResponseEntity<>("An Error has occurred", HttpStatus.NOT_FOUND);
  }
  
}
