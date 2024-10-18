package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
* Main function.
*
* 
*/
@SpringBootApplication
public class Main {
  /**
  * Main function.
  *
  * 
  */
  public static void main(String[] args) {
    // RouteRequestGoogle r = new RouteRequestGoogle("28-30 Jackson Ave,Long Island City,NY 11101",
    //     "116th and Broadway, New York, NY 10027");
    // r.getRequestEntity();
    // System.out.println(r.getRequestEntity());
    // SpringApplication.run(Main.class, args);

    SpringApplication.run(Main.class, args);
  }
  //   String connectionString = "mongodb+srv:
  //team_public:Hakunamatata@cluster4156.287dv.mongodb.net
  ///?retryWrites=true&w=majority&appName=Cluster4156";

        
  //       try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            
  //           MongoDatabase database = mongoClient.getDatabase("Hkunamatata_DB"); 

  //           System.out.println("Successfully connect to database: " + database.getName());
  //       } catch (Exception e) {
  //           System.err.println("Errors occured: " + e.getMessage());
  //           e.printStackTrace();
  //       }
  //   return;
  // }
}