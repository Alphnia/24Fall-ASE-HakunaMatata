package app;

import java.util.List;
import java.util.Map;
import java.util.Arrays;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.FindIterable;

import org.bson.Document;

import org.springframework.boot.system.ApplicationTemp;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class DatabaseOperation{
  public DatabaseOperation(String origin, String destination){
    String connectionString = "mongodb+srv://test_user:coms4156@cluster4156.287dv.mongodb.net/?retryWrites=true&w=majority&appName=Cluster4156";
    MongoClient mongoClient = MongoClients.create(connectionString);
    this.database = mongoClient.getDatabase("Hkunamatata_DB");
    this.collection = database.getCollection("Route");
    System.out.println("connect to server");
  }

  public String FindDocumentbyOriDes(String origin, String destination){
    try{
      List<String> OriDes = Arrays.asList(origin, destination);
      Document document = new Document("OriDes", OriDes);
      FindIterable<Document> results = this.collection.find(document).limit(1);
      System.out.println(results);
      MongoCursor<Document> cursor = results.iterator();
      if (cursor.hasNext()) {
        Document doc = cursor.next();
        System.out.println("Record found: " + doc.toJson());
        return doc.toJson();
      } else {
          System.out.println("No record found.");
          return null;
      }
      //return null;
    }catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
      return "An Error has occurred";
    }
    

  }

  public ResponseEntity<?> CreateDocument(String rawjson,String origin,
  String destination, String[] stoplist, String[] annotatedlist){
    try{
      List<String> OriDes = Arrays.asList(origin, destination);
      List<String> stoplistArray = Arrays.asList(stoplist);
      List<String> annotatedlistArray = Arrays.asList(annotatedlist);
      long count = this.collection.countDocuments();
      Document newDocument = new Document("RouteID", count)
                    .append("rawjson", rawjson)
                    .append("OriDes", OriDes)
                    .append("Stoplist", stoplistArray)
                    .append("Annotatedlist", annotatedlistArray);
      collection.insertOne(newDocument);
      System.out.println("Insert successfully!!!");
      return new ResponseEntity<>("Success",HttpStatus.OK);
    }catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
      return new ResponseEntity<>("Error in creating doc", HttpStatus.OK);
    }
    

  }

  private ResponseEntity<?> handleException(Exception e) {
    System.out.println(e.toString());
    return new ResponseEntity<>("An Error has occurred", HttpStatus.OK);
  }

  private MongoDatabase database;
  private MongoCollection<Document> collection;

}