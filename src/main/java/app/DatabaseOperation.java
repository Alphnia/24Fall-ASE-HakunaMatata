package app;

import java.util.List;
import java.util.Map;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.MongoCursor;

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
    String connectionString = "mongodb+srv://team_public:Hakunamatata@cluster4156.287dv.mongodb.net/?retryWrites=true&w=majority&appName=Cluster4156";
    MongoClient mongoClient = MongoClients.create(connectionString);
    this.database = mongoClient.getDatabase("Hkunamatata_DB");
    this.collection = database.getCollection("Route");
  }

  public String FindDocumentbyOriDes(String origin, String destination){
    try{
      String[] OriDes = new String[2];
      OriDes[0] = origin;
      OriDes[1] = destination;
      System.out.println("before");
      Document document = this.collection.find(eq("RouteID", 0)).first();
      if (document != null) {
          System.out.println("Record found: " + document.toJson());
      } else {
          System.out.println("No record found.");
      }
      return document.toJson();
    }catch (Exception e) {
      return "An Error has occurred";
    }
    

  }

  private ResponseEntity<?> handleException(Exception e) {
    System.out.println(e.toString());
    return new ResponseEntity<>("An Error has occurred", HttpStatus.OK);
  }

  private MongoDatabase database;
  private MongoCollection<Document> collection;

}