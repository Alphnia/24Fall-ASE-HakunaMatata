package app;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Get function.
 *
 * 
 */
@RestController
public class RouteController {

  private static final String API_URL = "https://routes.googleapis.com/directions/v2:computeRoutes";
  private static final String API_KEY = "AIzaSyDDe643HpTH5XXUBOZLNuJrcCFBdKM4k8Q"; // Your API key
  private static final String FIELD_MASK = "routes.legs.steps.transitDetails";

  /**
   * Post function.
   *
   * 
   */
  @PostMapping("/computeRoutes")
  public ResponseEntity<String> computeRoutes(@RequestBody Map<String, Object> routeRequest) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");
    headers.set("X-Goog-Api-Key", API_KEY);
    headers.set("X-Goog-FieldMask", FIELD_MASK);
    
    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(routeRequest, headers);

    try {
      RestTemplate restTemplate = new RestTemplate();
      ResponseEntity<String> response = restTemplate.exchange(
          API_URL,
          HttpMethod.POST,
          requestEntity,
          String.class
      );
      return response;
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
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
        DatabaseOperation database = new DatabaseOperation(origin, destination);
        String document = database.findDocumentbyOriDes(origin, destination);
        return new ResponseEntity<>(document, HttpStatus.OK);
      } else {
        if (origin != null && origin.matches("^[a-zA-Z0-9 .,-]+$")
          && destination != null && destination.matches("^[a-zA-Z0-9 .,-]+$")){
          RouteRequestGoogle routeRequest = new RouteRequestGoogle(origin, destination);
          Map<String, Object> entity = routeRequest.getRequestEntity();
          // just for test phase
          // FileReader reader = new FileReader("src/main/resources/googleResponse.json");
          // JsonObject jsonRead = JsonParser.parseReader(reader).getAsJsonObject();
          // ReadJson jsonResponse = new ReadJson(jsonRead.toString());
          ResponseEntity<String> googleResponse = computeRoutes(entity);
          if (googleResponse.getStatusCode() == HttpStatus.NOT_FOUND){
            return new ResponseEntity<>("Address not found.", HttpStatus.NOT_FOUND);
          }
          ReadJson jsonResponse = new ReadJson(googleResponse.getBody());
          String[] stopList = jsonResponse.getContent();
          
          // JsonObject rawJsonToy = new JsonObject();
          String rawJsonToy = "";
          createRoute(rawJsonToy, origin, destination, stopList, stopList);
          return new ResponseEntity<>("Successfully Created!", HttpStatus.OK);
        } else{
          return new ResponseEntity<>("Invalid Inputs!", HttpStatus.BAD_REQUEST);
        }
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
      if (origin != null && origin.matches("^[a-zA-Z0-9 ,.-]+$")
        && destination != null && destination.matches("^[a-zA-Z0-9 ,.-]+$")){
        DatabaseOperation database = new DatabaseOperation(origin, destination);
        String document = database.findDocumentbyOriDes(origin, destination);
        if (document != null) {
          return new ResponseEntity<>(HttpStatus.OK);
        } else {
          return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
      } else{
        return new ResponseEntity<>("Invalid Inputs!", HttpStatus.BAD_REQUEST);
      }
      
    } catch (Exception e) {
      return handleException(e);
    }
  }

  /**
   * Returns the details of the specified route.
   * 
   *
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
      @RequestParam("stoplist") String[] stoplist,
      @RequestParam("annotatedlist") String[] annotatedlist) {
    try {
      DatabaseOperation database = new DatabaseOperation(origin, destination);
      boolean response;
      response = database.createDocument(rawjson, origin, destination,
      stoplist, annotatedlist).getStatusCode() == HttpStatus.OK;
      if (response) {
        return new ResponseEntity<>("New Route Created.", HttpStatus.OK);
      } else {
        return new ResponseEntity<>("Insertion Failed", HttpStatus.NOT_FOUND);
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
  @DeleteMapping(value = "/deleteRoute", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> deleteRoute(@RequestParam("origin") String origin,
      @RequestParam("destination") String destination) {
    try {
      DatabaseOperation database = new DatabaseOperation(origin, destination);
      boolean response;
      response = database.deleteDocument(origin, destination).getStatusCode() == HttpStatus.OK;
      if (response) {
        return new ResponseEntity<>("Successfully deleted", HttpStatus.OK);
      } else {
        return new ResponseEntity<>("Failed", HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      return handleException(e);
    }
  }

  /**
   * Inserts a new annotation document into the MongoDB collection for testing.
   * Now just for developer testing
   *
   * @return A ResponseEntity with a success message if the insertion is successful, 
   *         or a BAD_REQUEST status if an error occurs.
   */
  @GetMapping("/insertAnnoForTest")
  public ResponseEntity<?> insertAnno() {

    String connectionString = 
        "mongodb+srv://test_user:coms4156@cluster4156.287dv.mongodb.net/"
        + "?retryWrites=true&w=majority&appName=Cluster4156";
    try (MongoClient mongoClient = MongoClients.create(connectionString)) {
      Map<String, String> mp = new HashMap<String, String>();
      mp.put("direction", "turn right at the first corner.");
      Map<String, Object> node = new HashMap<String, Object>();
      node.put("1", mp);
      mp.clear();
      mp.put("direction", "go straight until you see a traffic light.");
      Map<String, Object> node2 = new HashMap<String, Object>();
      node2.put("2", mp);
      List<Map<String, Object>> stoplist = new ArrayList<Map<String, Object>>();
      stoplist.add(node);
      stoplist.add(node2);
      MongoDatabase database = mongoClient.getDatabase("Hkunamatata_DB"); 
      MongoCollection<Document> collection = database.getCollection("Annotation");
      long count = collection.countDocuments();
      Document newDocument = new Document("AnnoID", count)
                    .append("RouteID", 1)
                    .append("UserID", "670c4dab7013573300601f64")
                    .append("Stoplist", stoplist);
      try {
        collection.insertOne(newDocument);

        return new ResponseEntity<>("Insert complete", HttpStatus.OK);

      } catch (Exception e) {
        return new ResponseEntity<>(".", HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      return handleException(e);
    }
  }

  /**
   * Checks if a route and corresponding annotation exist based on the provided routeId and userId.
   *
   * @param routeId The Id of the route to check.
   * @param userId The Id of the user associated with the annotation.
   * @return A ResponseEntity with an OK status if the annotation exists, 
   *         or NOT_FOUND if either the route or annotation is missing.
   */
  @GetMapping(value = "/checkAnno", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> checkAnnos(@RequestParam("routeId") String routeId,
        @RequestParam("userId") String userId) {
    try {
      DatabaseOperation db = new DatabaseOperation(true, routeId, userId);
      String route = db.findRoutebyIds(routeId);
      if (route != null) {
        // System.out.println("This route exists:" + OriDes);
        // System.out.println(foundDocument.toJson());
        String annotation = db.findAnnotationbyIds(routeId, userId);
        if (annotation != null) {
          return new ResponseEntity<>(HttpStatus.OK);
        } else {
          return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
      } else {
        System.out.println("No route Found:");
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      return handleException(e);
    }
  }

  /**
   * Edits or creates a new annotation document based on the provided routeId and userId. 
   * If the annotation exists, it updates the stop list. If it does not exist, a new one is created.
   *
   * @param routeId The Id of the route.
   * @param userId The Id of the user associated with the annotation.
   * @param stopList The list of stops to be added or updated in the annotation.
   * @return A ResponseEntity with a success message if the operation is successful, 
   *         or a BAD_REQUEST status if an error occurs.
   */
  @PatchMapping("/editRoute")
  public ResponseEntity<?>  editRoute(
        @RequestParam(value = "routeId") String routeId,
        @RequestParam(value = "userId") String userId,
        @RequestBody List<Map<String, Object>> stopList) {
    boolean doesExist = (checkAnnos(routeId, userId).getStatusCode() == HttpStatus.OK);

    DatabaseOperation db = new DatabaseOperation(true, routeId, userId);
    if (doesExist) {
      // edit
      String result = db.updateAnno(routeId, userId, stopList);
      if ("Update success".equals(result)) {
        return new ResponseEntity<>(result, HttpStatus.OK);
      } else {
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
      }
    } else {
      // create
      String result = db.insertAnno(routeId, userId, stopList);
      if ("Insert success".equals(result)) {
        return new ResponseEntity<>(result, HttpStatus.OK);
      } else {
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
      }
    }
  }

  /**
   * Deletes an annotation document based on the provided routeId and userId.
   *
   * @param routeId The Id of the route.
   * @param userId The Id of the user associated with the annotation.
   * @return A ResponseEntity with a success message if the deletion is successful, 
   *         NOT_FOUND if the annotation does not exist, or a BAD_REQUEST status if an error occurs.
   */
  @DeleteMapping("/deleteAnno")
  public ResponseEntity<?> deleteAnnotation(
      @RequestParam("routeId") String routeId,
      @RequestParam("userId") String userId) {
    try {
      DatabaseOperation db = new DatabaseOperation(true, routeId, userId);
      String result = db.deleteAnno(routeId, userId);

      if ("Delete success".equals(result)) {
        return new ResponseEntity<>(result, HttpStatus.OK);
      } else if ("Annotation not found".equals(result)) {
        return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
      } else {
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
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