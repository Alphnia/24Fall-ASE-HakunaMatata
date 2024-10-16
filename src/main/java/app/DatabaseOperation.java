package app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

public class DatabaseOperation{
  
  public DatabaseOperation(String origin, String destination){
    String connectionString = "mongodb+srv://test_user:coms4156@cluster4156.287dv.mongodb.net/?retryWrites=true&w=majority&appName=Cluster4156";
    MongoClient mongoClient = MongoClients.create(connectionString);
    this.database = mongoClient.getDatabase("Hkunamatata_DB");
    this.collection = database.getCollection("Route");
  }

  public String FindDocumentbyOriDes(String origin, String destination){
    try{
      List<String> OriDes = Arrays.asList(origin, destination);
      System.out.println("before");
      Document document = new Document("OriDes", OriDes);
      FindIterable<Document> results = this.collection.find(document).limit(1);
      if (document != null) {
          System.out.println("Record found: " + document.toJson());
      } else {
          System.out.println("No record found.");
      }
      return document.toJson();
    }catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
      return "An Error has occurred";
    }
    

  }

  public DatabaseOperation(Boolean flag, String RouteID, String UserID){
    String connectionString = "mongodb+srv://test_user:coms4156@cluster4156.287dv.mongodb.net/?retryWrites=true&w=majority&appName=Cluster4156";
    MongoClient mongoClient = MongoClients.create(connectionString);
    this.database = mongoClient.getDatabase("Hkunamatata_DB");
    this.collection = database.getCollection("Route");
    this.collection2 = database.getCollection("Annotation");
  }
  
  public String FindRoutebyIDs(String RouteID){
    try{
      
      Document query = new Document("RouteID", RouteID);
      FindIterable<Document> results = this.collection.find(query).limit(1);
      Document document = results.first();
      if (document != null) {
          System.out.println("Record found: " + document.toJson());
          return document.toJson();
      } else {
          System.out.println("No record found.");
          return null;
      }
    }catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
      return "An Error has occurred";
    }
    

  }

  public String FindAnnotationbyIDs(String RouteID, String UserID){
    try{
      List<String> IDs = Arrays.asList(RouteID, UserID);
      Document query = new Document("IDs", IDs);
      FindIterable<Document> results = this.collection.find(query).limit(1);
      Document document = results.first();
      if (document != null) {
          System.out.println("Record found: " + document.toJson());
          return document.toJson();
      } else {
          System.out.println("No record found.");
          return null;
      }
    }catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
      return "An Error has occurred";
    }
    

  }
  public String UpdateAnno(String RouteID, String UserID, List<Map<String, Object>> stopList){
    try {
      collection2.updateOne(
        Filters.and(Filters.eq("RouteID", RouteID), Filters.eq("UserID", UserID)),
        Updates.set("Stoplist", stopList));
        return "Update success";
    } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
        return "An Error has occurred";
    }
  }
  public String InsertAnno(String RouteID, String UserID, List<Map<String, Object>> stopList){
    try {
      long count = collection2.countDocuments();
      Document Doc = new Document("AnnoID", count)
                    .append("RouteID", RouteID)
                    .append("UserID", UserID)
                    .append("Stoplist", stopList);
      collection2.insertOne(Doc);
      return "Insert success";
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