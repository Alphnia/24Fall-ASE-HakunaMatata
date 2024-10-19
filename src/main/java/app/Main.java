
package app;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Main function.
 */
@SpringBootApplication
public class Main {
  public static void main(String[] args) {
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

