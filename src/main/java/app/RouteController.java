package app;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
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
        Document document = database.findDocumentbyOriDes(origin, destination);
        // System.out.println("Type of first stoplist: " + document.getList("Stoplist", Object.class).get(0).getClass().getName());
        return new ResponseEntity<>(document, HttpStatus.OK);
      } else {
        if (origin != null && origin.matches("^[a-zA-Z0-9 .,-]+$")
            && destination != null && destination.matches("^[a-zA-Z0-9 .,-]+$")) {
          RouteRequestGoogle routeRequest = new RouteRequestGoogle(origin, destination);
          Map<String, Object> entity = routeRequest.getRequestEntity();
          // just for test phase
          // FileReader reader = new FileReader("src/main/resources/googleResponse.json");
          // JsonObject jsonRead = JsonParser.parseReader(reader).getAsJsonObject();
          // ReadJson jsonResponse = new ReadJson(jsonRead.toString());
          ResponseEntity<String> googleResponse = computeRoutes(entity);
          if (googleResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
            return new ResponseEntity<>("Address not found.", HttpStatus.NOT_FOUND);
          }
          ReadJson jsonResponse = new ReadJson(googleResponse.getBody());
          String[] stopList = jsonResponse.getContent();
          
          // JsonObject rawJsonToy = new JsonObject();
          String rawJsonToy = "";
          String[] annoList = new String[0];
          createRoute(rawJsonToy, origin, destination, stopList, annoList);
          return new ResponseEntity<>("No Record Found. Successfully Created!", HttpStatus.OK);
        } else {
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
          && destination != null && destination.matches("^[a-zA-Z0-9 ,.-]+$")) {
        DatabaseOperation database = new DatabaseOperation(origin, destination);
        Document document = database.findDocumentbyOriDes(origin, destination);
        if (document != null) {
          return new ResponseEntity<>(HttpStatus.OK);
        } else {
          return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
      } else {
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
   * Query a photo annotation document into the MongoDB collection for testing.
   *
   * @return A ResponseEntity with a url if the query returns a item,
   *         or a NOT_FOUND if not exists,
   *         or a BAD_REQUEST status if an error occurs.
   */

  @GetMapping(value = "/queryPhotoAnno", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> queryPhotoAnno(@RequestParam("routeId") String routeId,
                                    @RequestParam("userId") String userId,
                                    @RequestParam("address") String addr) {
    try {
      // Validate inputs: Check for null, empty, or invalid characters
      if (routeId != null && routeId.matches("^[a-fA-F0-9]+$") 
          && userId != null && userId.matches("^[a-fA-F0-9]+$")) {
        String connectionString = 
            "mongodb+srv://test_user:coms4156@cluster4156.287dv.mongodb.net"
            + "/?retryWrites=true&w=majority&appName=Cluster4156&tsl=true";
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
        
          MongoDatabase database = mongoClient.getDatabase("Hkunamatata_DB"); 
          MongoCollection<Document> collection = database.getCollection("Photo_annotation");
          Document query = new Document("RouteID", routeId)
                .append("UserID", userId).append("Address", addr);
          FindIterable<Document> results = collection.find(query).limit(1);
            
          Document doc = results.first();
          if (doc != null) {
            String url = doc.getString("Url");
            return new ResponseEntity<>(url, HttpStatus.OK);
          } else {
            return new ResponseEntity<>("Annotation not found", HttpStatus.NOT_FOUND);
          }
        }
      } else {
        // Return BAD_REQUEST if inputs are invalid
        return new ResponseEntity<>("Invalid input: routeId and userId must"
              + "contain only alphanumeric characters, underscores, or hyphens", 
                                    HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      return handleException(e);
    }
  }
  
/**
   * Inserts a new photo annotation document into the MongoDB collection.
   *
   * @param photoAnnotation A {@code PhotoAnnotation} object containing the
   *                        photo annotation data.
   * @return A {@code ResponseEntity} with a success message if the insertion
   *         is successful, or a BAD_REQUEST status if an error occurs.
   */
  @PostMapping("/insertPhotoAnno")
  public ResponseEntity<String> insertPhotoAnno(@RequestBody PhotoAnnotation photoAnnotation) {

    if (photoAnnotation.getRouteId() == null || !photoAnnotation.getRouteId().matches("^[a-fA-F0-9]+$") ||
        photoAnnotation.getUserId() == null || !photoAnnotation.getUserId().matches("^[a-fA-F0-9]+$") ||
        photoAnnotation.getUrl() == null || photoAnnotation.getUrl().trim().isEmpty() ||
        photoAnnotation.getAddress() == null || !photoAnnotation.getAddress().matches("^[a-zA-Z0-9 .,-]+$")) {
      return new ResponseEntity<>("Invalid Inputs!", HttpStatus.BAD_REQUEST);
    }

    String connectionString = 
        "mongodb+srv://test_user:coms4156@cluster4156.287dv.mongodb.net/?retryWrites=true&w=majority&appName=Cluster4156&tsl=true";
    try (MongoClient mongoClient = MongoClients.create(connectionString)) {
     
      MongoDatabase database = mongoClient.getDatabase("Hkunamatata_DB"); 
      MongoCollection<Document> collection = database.getCollection("Photo_annotation");
      
      Document newDocument = new Document("RouteID", photoAnnotation.getRouteId())
                            .append("Url", photoAnnotation.getUrl())
                            .append("UserID", photoAnnotation.getUserId())
                            .append("Address", photoAnnotation.getAddress());
      try {
        collection.insertOne(newDocument);
        return new ResponseEntity<>("Successfully Created!", HttpStatus.OK);
      } catch (Exception e) {
        return new ResponseEntity<>("Failed to insert annotation.", HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      return new ResponseEntity<>("Failed to connect to database.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
  /**
   * DTO class for Photo Annotation data.
   */
  public static class PhotoAnnotation {
    private String routeId;
    private String url;
    private String userId;
    private String address;

    // Getters and Setters

    public String getRouteId() {
      return routeId;
    }

    public void setRouteId(String routeId) {
      this.routeId = routeId;
    }

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    public String getUserId() {
      return userId;
    }

    public void setUserId(String userId) {
      this.userId = userId;
    }

    public String getAddress() {
      return address;
    }

    public void setAddress(String address) {
      this.address = address;
    }
  }
  /**
   * Checks if a route and corresponding annotation exist based on the provided routeId and userId.
   *
   * @param routeId A {@code String} representing the route's unique identifier.
   * @param userId A {@code String} representing the user's unique identifier.
   * 
   * @return A {@code ResponseEntity} object containing either an HTTP 200 status if the annotation 
   *         exists or a 404 NOT FOUND if either the route or annotation is missing. 
   *         If inputs are invalid, 
   *         returns a 400 BAD REQUEST.
   */
  @GetMapping(value = "/checkAnno", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> checkAnnos(@RequestParam("routeId") String routeId,
                                      @RequestParam("userId") String userId) {
    try {
      // Validate inputs: Check for null, empty, or invalid characters
      if (routeId != null && routeId.matches("^[a-fA-F0-9]+$")
          && userId != null && userId.matches("^[a-fA-F0-9]+$")) {

        // Proceed with database operations if inputs are valid
        DatabaseOperation db = new DatabaseOperation(true, routeId, userId);
        String route = db.findRoutebyIds(routeId);

        if (route != null) {
          String annotation = db.findAnnotationbyIds(routeId, userId);
          if (annotation != null) {
            return new ResponseEntity<>(HttpStatus.OK);
          } else {
            return new ResponseEntity<>("Annotation not found", HttpStatus.NOT_FOUND);
          }
        } else {
          return new ResponseEntity<>("Route not found", HttpStatus.NOT_FOUND);
        }
      } else {
        // Return BAD_REQUEST if inputs are invalid
        return new ResponseEntity<>("Invalid input: routeId and userId must contain only"
              + "alphanumeric characters, underscores, or hyphens", 
                                    HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      return handleException(e);
    }
  }

  /**
   * Edits or creates a new annotation document based on the provided routeId and userId. 
   * If the annotation exists, it updates the stop list. If it does not exist, a new one is created.
   *
   * @param routeId A {@code String} representing the route's unique identifier.
   * @param userId A {@code String} representing the user's unique identifier.
   * @param stopList A {@code List} of stops to be added or updated in the annotation.
   * @return A {@code ResponseEntity} object containing either:
   *         a success message with HTTP 200 status
   *         or a relevant error message with the appropriate status code.
   */
  @PatchMapping("/editRoute")
  public ResponseEntity<?> editRoute(
        @RequestParam(value = "routeId") String routeId,
        @RequestParam(value = "userId") String userId,
        @RequestBody List<Map<String, Object>> stopList) {

    try {
      // 1. Validate inputs: Check for null, empty values, and malformed stopList
      if (routeId == null || !routeId.matches("^[a-fA-F0-9]+$")
          || userId == null || !userId.matches("^[a-fA-F0-9]+$")
          || stopList == null || stopList.isEmpty()) {
        return new ResponseEntity<>("Invalid input: routeId and userId must contain"
              + " only alphanumeric characters, underscores, or hyphens. StopList cannot be empty.",
                                    HttpStatus.BAD_REQUEST);
      }

      // 2. Check if the annotation exists
      boolean doesExist = (checkAnnos(routeId, userId).getStatusCode() == HttpStatus.OK);

      // 3. Initialize DatabaseOperation
      DatabaseOperation db = new DatabaseOperation(true, routeId, userId);

      if (doesExist) {
        // Edit existing annotation
        String result = db.updateAnno(routeId, userId, stopList);
        if ("Update success".equals(result)) {
          return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
          return new ResponseEntity<>("Failed to update annotation", HttpStatus.BAD_REQUEST);
        }
      } else {
        // Create new annotation
        String result = db.insertAnno(routeId, userId, stopList);
        return new ResponseEntity<>(result, HttpStatus.OK);
      }
    } catch (Exception e) {
      return handleException(e);
    }
  }


  /**
   * Deletes an annotation document based on the provided routeId and userId.
   *
   * @param routeId A {@code String} representing route's unique identifier.
   * @param userId A {@code String} representing user's unique identifier.
   * @return A {@code ResponseEntity} object containing either:
   *         a success message with HTTP 200 status, 
   *         NOT_FOUND if the annotation does not exist, 
   *         or a BAD_REQUEST status if input validation fails.
   */
  @DeleteMapping("/deleteAnno")
  public ResponseEntity<?> deleteAnnotation(
      @RequestParam("routeId") String routeId,
      @RequestParam("userId") String userId) {
    try {
      // 1. Validate inputs: Ensure routeId and userId are not null, empty, or invalid characters
      if (routeId == null || !routeId.matches("^[a-fA-F0-9]+$")
          || userId == null || !userId.matches("^[a-fA-F0-9]+$")) {
        return new ResponseEntity<>(
            "Invalid input: routeId and userId must"
                  + " contain only alphanumeric characters, underscores, or hyphens.",
                  HttpStatus.BAD_REQUEST);
      }

      // 2. Proceed with deletion if inputs are valid
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

  /**
   * Handles exceptions and returns a standard NOT_FOUND response with an error message.
   *
   * @param e The exception that was thrown.
   * @return A {@code ResponseEntity} with an error message and NOT_FOUND status.
   */
  private ResponseEntity<?> handleException(Exception e) {
    System.out.println(e.toString());
    return new ResponseEntity<>("An Error has occurred", HttpStatus.NOT_FOUND);
  }
}
