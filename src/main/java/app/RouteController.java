package app;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * All endpoints are implemented in this class.
 */
@RestController
public class RouteController {

  private static final String API_URL = "https://routes.googleapis.com/directions/v2:computeRoutes";
  private static final String API_KEY = "AIzaSyDDe643HpTH5XXUBOZLNuJrcCFBdKM4k8Q"; // Your API key
  private static final String FIELD_MASK = "routes.legs.steps.transitDetails";
  
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