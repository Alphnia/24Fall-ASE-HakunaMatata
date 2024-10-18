package app;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
          
@RestController
public class RouteController {

  private static final String API_URL = "https://routes.googleapis.com/directions/v2:computeRoutes";
  private static final String API_KEY = "AIzaSyDDe643HpTH5XXUBOZLNuJrcCFBdKM4k8Q"; // Your API key
  private static final String FIELD_MASK = "routes.legs.steps.transitDetails";
  
  @GetMapping("/insertAnnoForTest")
  public ResponseEntity<?> insertAnno() {

    String connectionString = "mongodb+srv://test_user:coms4156@cluster4156.287dv.mongodb.net/?retryWrites=true&w=majority&appName=Cluster4156";
    try (MongoClient mongoClient = MongoClients.create(connectionString)) {
      MongoDatabase database = mongoClient.getDatabase("Hkunamatata_DB"); 
      MongoCollection<Document> collection = database.getCollection("Annotation");
      Map<String, String> mp = new HashMap<String, String>();
      mp.put("direction", "turn right at the first corner.");
      Map<String, Object> node = new HashMap<String, Object>();
      node.put("1", mp);
      mp.clear();
      mp.put("direction","go straight until you see a traffic light.");
      Map<String, Object> node2 = new HashMap<String, Object>();
      node2.put("2", mp);
      List<Map<String, Object>> stoplist = new ArrayList<Map<String, Object>>();
      stoplist.add(node);
      stoplist.add(node2);
      long count = collection.countDocuments();
      Document newDocument = new Document("AnnoID", count)
                    .append("RouteID", 1)
                    .append("UserID", "670c4dab7013573300601f64")
                    .append("Stoplist", stoplist);
      try {
        collection.insertOne(newDocument);

        return new ResponseEntity<>("Insert complete", HttpStatus.OK);

      }catch (Exception e) {
        return new ResponseEntity<>(".", HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      return handleException(e);
    }
  }


  @GetMapping(value = "/checkAnno", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> checkAnnos(@RequestParam("routeID") String routeID,
        @RequestParam("userID") String userID) {
    try {
      DatabaseOperation DB = new DatabaseOperation(true, routeID, userID);
      String route = DB.FindRoutebyIDs(routeID);
      if (route != null) {
        // System.out.println("This route exists:" + OriDes);
        // System.out.println(foundDocument.toJson());
        String annotation = DB.FindAnnotationbyIDs(routeID, userID);
        if (annotation != null){
          return new ResponseEntity<>(HttpStatus.OK);
        }
        else 
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      } else {
        System.out.println("No route Found:");
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      return handleException(e);
    }
  }
  @PatchMapping("/editRoute")
  public ResponseEntity<?>  editRoute(
        @RequestParam(value = "routeID") String routeID,
        @RequestParam(value = "userID") String userID,
        @RequestBody List<Map<String, Object>> stopList) {
    boolean doesExist = (checkAnnos(routeID, userID).getStatusCode() == HttpStatus.OK);

    DatabaseOperation db = new DatabaseOperation(true, routeID, userID);
    if (doesExist){
      // edit
      String result = db.UpdateAnno(routeID, userID, stopList);
      if ("Update success".equals(result)){
        return new ResponseEntity<>(result, HttpStatus.OK);
      }
      else
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }
    else{
      // create
      String result = db.InsertAnno(routeID, userID, stopList);
      if ("Insert success".equals(result)){
        return new ResponseEntity<>(result, HttpStatus.OK);
      }
      else{
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
      }
    }
  }

  @DeleteMapping("/deleteAnno")
  public ResponseEntity<?> deleteAnnotation(
      @RequestParam("routeID") String routeID,
      @RequestParam("userID") String userID) {
    try {
      DatabaseOperation db = new DatabaseOperation(true, routeID, userID);
      String result = db.DeleteAnno(routeID, userID);

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