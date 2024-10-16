package app;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileReader;
import java.util.Map;
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
        DatabaseOperation database = new DatabaseOperation(origin, destination);
        String document = database.findDocumentbyOriDes(origin, destination);
        return new ResponseEntity<>(document, HttpStatus.OK);
      } else {
        RouteRequestGoogle routeRequest = new RouteRequestGoogle(origin, destination);
        Map<String, Object> entity = routeRequest.getRequestEntity();
        // just for test phase
        FileReader reader = new FileReader("src/main/resources/googleResponse.json");
        JsonObject jsonRead = JsonParser.parseReader(reader).getAsJsonObject();
        // ResponseEntity<String> googleResponse = computeRoutes(entity);
        // ReadJSON jsonResponse = new ReadJSON(googleResponse.getBody());
        ReadJson jsonResponse = new ReadJson(jsonRead.toString());
        String[] stopList = jsonResponse.getContent();
        // JsonObject rawJsonToy = new JsonObject();
        String rawJsonToy = "";
        createRoute(rawJsonToy, origin, destination, stopList, stopList);
        return new ResponseEntity<>("Successfully Created!", HttpStatus.OK);
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
      DatabaseOperation database = new DatabaseOperation(origin, destination);
      String document = database.findDocumentbyOriDes(origin, destination);
      if (document != null) {
        return new ResponseEntity<>(HttpStatus.OK);
      } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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

  private ResponseEntity<?> handleException(Exception e) {
    return new ResponseEntity<>("An Error has occurred", HttpStatus.NOT_FOUND);
  }
  
}
