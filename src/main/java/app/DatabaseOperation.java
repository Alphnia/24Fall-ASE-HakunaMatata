package app;

import static com.mongodb.client.model.Filters.eq;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;



/**
 * database operation for annotation.
 */
public class DatabaseOperation {
  /**
   * Constructor that initializes a DatabaseOperation instance with a MongoDB connection
   * and retrieves the "Route" and "Annotation" collections based on route Id and user Id.
   *
   * @param flag A boolean flag.
   * 
   * @param routeId The route Id.
   * @param userId The user Id.
   */
  public DatabaseOperation(Boolean flag, String routeId, String userId) {
    String connectionString = 
        "mongodb+srv://test_user:coms4156@cluster4156.287dv.mongodb.net/"
        + "?retryWrites=true&w=majority&appName=Cluster4156&tsl=true";
    MongoClient mongoClient = MongoClients.create(connectionString);
    this.database = mongoClient.getDatabase("Hkunamatata_DB");
    this.collection = database.getCollection("Route");
    this.collection2 = database.getCollection("Annotation");
  }


  /**
  * DatabaseOperation initialization.
  *
  * 
  */
  public DatabaseOperation(String origin, String destination) {
    String connectionString = 
        "mongodb+srv://test_user:coms4156@cluster4156.287dv.mongodb.net/"
        + "?retryWrites=true&w=majority&appName=Cluster4156&tsl=true";
    MongoClient mongoClient = MongoClients.create(connectionString);
    this.database = mongoClient.getDatabase("Hkunamatata_DB");
    this.collection = database.getCollection("Route");
  }

  /**
  * DatabaseOperation initialization.
  *
  * 
  */
  public DatabaseOperation(String collection) {
    String connectionString = 
        "mongodb+srv://test_user:coms4156@cluster4156.287dv.mongodb.net/"
        + "?retryWrites=true&w=majority&appName=Cluster4156&tsl=true";
    MongoClient mongoClient = MongoClients.create(connectionString);
    this.database = mongoClient.getDatabase("Hkunamatata_DB");
    this.collection = database.getCollection(collection);
  }
  
  /**
  * findDocumentbyOriDes function.
  *
  * 
  */
  public String findDocumentbyOriDes(String origin, String destination) {
    try {
      List<String> orides = Arrays.asList(origin, destination);
      Document document = new Document("OriDes", orides);
      FindIterable<Document> results = this.collection.find(document).limit(1);
      MongoCursor<Document> cursor = results.iterator();
      if (cursor.hasNext()) {
        Document doc = cursor.next();
        return doc.toJson();
      } else {
        return null;
      }
    } catch (Exception e) {
      return "An Error has occurred";
    }
    

  }

  /**
  * getUserIdByAnnoId function.
  *
  * 
  */
  public String getUserIdByAnnoId(int annoId) {
    try {
      Document query = new Document("AnnoID", annoId);
      FindIterable<Document> results = this.collection.find(query).limit(1);
      MongoCursor<Document> cursor = results.iterator();
      if (cursor.hasNext()) {
        Document document = cursor.next();
        return document.getString("UserID");
      } else {
        return null;
      }
    } catch (Exception e) {
      return "An Error has occurred";
    }
  }

  /**
  * createDocument function.
  *
  * 
  */
  public ResponseEntity<?> createDocument(String rawjson, String origin,
      String destination, String[] stoplist, String[] annotatedlist) {
    try {
      List<String> orides = Arrays.asList(origin, destination);
      List<String> stoplistArray = Arrays.asList(stoplist);
      List<String> annotatedlistArray = Arrays.asList(annotatedlist);
      long count = this.collection.countDocuments();
      Document newDocument = new Document("RouteID", count)
                    .append("rawjson", rawjson)
                    .append("OriDes", orides)
                    .append("Stoplist", stoplistArray)
                    .append("Annotatedlist", annotatedlistArray);
      collection.insertOne(newDocument);
      return new ResponseEntity<>("Success", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Error in creating doc", HttpStatus.NOT_FOUND);
    }
    

  }

  /**
  * createDocument function.
  *
  * 
  */
  public ResponseEntity<?> createDocument_track(String userId, Double latitude,
      Double longitude, DateTimeFormatter timestamp) {
    try {
      List<Double> location = Arrays.asList(latitude, longitude);
      Document doc = new Document("UserID", userId)
          .append("Location", location)
          .append("timestamp", timestamp);
      this.collection.insertOne(doc);
      System.out.println("hi ashley: ");
      return new ResponseEntity<>("Success", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Error in creating doc", HttpStatus.NOT_FOUND);
    }

  }

  /**
   * Get the latest location of user A.
   *
   *
   */
  public Map<String, Object> getLatestLocation(String userId) {
    try {
      Document query = new Document("UserID", userId); // Fix case sensitivity
      Document sort = new Document("Timestamp", -1); // Fix case sensitivity
      Document result = collection.find(query).sort(sort).first();

      if (result != null) {
        List<Double> locationArray = result.getList("Location", Double.class);
        double latitude = locationArray.get(0);
        double longitude = locationArray.get(1);

        Instant timestamp = result.getDate("Timestamp").toInstant();
        String formattedTimestamp = DateTimeFormatter.ISO_OFFSET_DATE_TIME
            .withZone(ZoneId.systemDefault())
            .format(timestamp);

        return Map.of(
            "latitude", latitude,
            "longitude", longitude,
            "timestamp", formattedTimestamp
        );
      }
      return null;
    } catch (Exception e) {
      throw new RuntimeException("Failed to retrieve location data: " + e.getMessage());
    }
  }


  /**
  * deleteDocument function.
  *
  * 
  */
  public ResponseEntity<?> deleteDocument(String origin, String destination) {
    try {
      List<String> orides = Arrays.asList(origin, destination);
      DeleteResult result = this.collection.deleteOne(eq("OriDes", orides));
      if (result.getDeletedCount() > 0) {
        return new ResponseEntity<>("Successfully deleted", HttpStatus.OK);
      } else {
        return new ResponseEntity<>("Error in deleting doc",  HttpStatus.NOT_FOUND);
      }


    } catch (Exception e) {
      return new ResponseEntity<>("Error in deleting doc", HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Finds a route document in the "Route" collection by the route Id.
   *
   * @param routeId The route Id to search for.
   * @return The JSON representation of the found document, or null if not found.
   */
  public String findRoutebyIds(String routeId) {
    try {
      int routeIdInt = Integer.parseInt(routeId);
      Document query = new Document("RouteID", routeIdInt);
      FindIterable<Document> results = this.collection.find(query).limit(1);
      Document document = results.first();
      if (document != null) {
        System.out.println("Record found: " + document.toJson());
        return document.toJson();
      } else {
        System.out.println("No record found.");
        return null;
      }
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
      return "An Error has occurred";
    }
  }

  /**
   * Finds an annotation document in the "Annotation" collection by the route Id and user Id.
   *
   * @param routeId The route Id to search for.
   * @param userId The user Id to search for.
   * @return The JSON representation of the found document, or null if not found.
   */
  public String findAnnotationbyIds(String routeId, String userId) {
    try {
      // List<String> ids = Arrays.asList(routeId, userId);
      Document query = new Document("RouteID", routeId).append("UserID", userId);
      FindIterable<Document> results = this.collection2.find(query).limit(1);
      Document document = results.first();
      if (document != null) {
        System.out.println("Record found: " + document.toJson());
        return document.toJson();
      } else {
        System.out.println("No record found.");
        return null;
      }
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
      return "An Error has occurred";
    }
  }

  /**
   * Updates the annotation for a given route and user Id with a new stop list.
   *
   * @param routeId The route Id to update.
   * @param userId The user Id to update.
   * @param stopList The new stop list to be updated.
   * @return "Update success" if the update is successful, or an error message.
   */
  public String updateAnno(String routeId, String userId, List<Map<String, Object>> stopList) {
    try {
      collection2.updateOne(
          Filters.and(Filters.eq("RouteID", routeId), Filters.eq("UserID", userId)),
          Updates.set("Stoplist", stopList));
      return "Update success";
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
      return "An Error has occurred";
    }
  }

  /**
   * Inserts a new annotation document into the "Annotation" collection.
   *
   * @param routeId The route Id of the annotation.
   * @param userId The user Id of the annotation.
   * @param stopList The stop list to be inserted.
   * @return "Insert success" if the insertion is successful, or an error message.
   */
  public String insertAnno(String routeId, String userId, List<Map<String, Object>> stopList) {
    try {
      long count = collection2.countDocuments();
      Document doc = new Document("AnnoID", count)
                    .append("RouteID", routeId)
                    .append("UserID", userId)
                    .append("Stoplist", stopList);
      collection2.insertOne(doc);
      return "Insert success";
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
      return "An Error has occurred";
    }
  }

  /**
   * Deletes an annotation document from the "Annotation" collection based on route Id and user Id.
   *
   * @param routeId The route Id to search for.
   * @param userId The user Id to search for.
   * @return "Delete success" if the annotation is deleted, 
   *         "Annotation not found" if no matching annotation is found,
   *         or an error message.
   */
  public String deleteAnno(String routeId, String userId) {
    try {
      Document query = new Document("RouteID", routeId).append("UserID", userId);
      long deletedCount = collection2.deleteOne(query).getDeletedCount();
      if (deletedCount > 0) {
        return "Delete success";
      } else {
        return "Annotation not found";
      }
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
      return "An Error has occurred";
    }
  }

  private ResponseEntity<?> handleException(Exception e) {
    System.out.println(e.toString());
    return new ResponseEntity<>("An Error has occurred", HttpStatus.OK);
  }


  private MongoDatabase database;
  private MongoCollection<Document> collection;
  private MongoCollection<Document> collection2;

}
