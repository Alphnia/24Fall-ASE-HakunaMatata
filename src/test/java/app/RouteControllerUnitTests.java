package app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Map;
import org.bson.BsonRegularExpression;
import org.bson.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;



/** 
 * This is the unit test class.
*/
@SpringBootTest
@ContextConfiguration
public class RouteControllerUnitTests {
  /**
   * The setupRouteForTesting is used to set up instances for testing.
   *
   */
  @BeforeAll
  public static void setupRouteForTesting() {
    originExists = "116th and Broadway, New York";
    destinationExists = "20 W 34th St., New York";
    originNotExists = "E 73rd St, New York, NY 10021"; 
    destinationNotExists = "162-124 E Broadway, New York, NY 10002"; 
    testRc = new RouteController();
    routeRequest = new RouteRequestGoogle(originExists, destinationExists);
  }

  @Test
  public void retrieveRouteExistsTest() {
    ResponseEntity<?> response = testRc.retrieveRoute(originExists, destinationExists);
    Document document = new Document("_id", new Document("$oid", "670e88a06240d7ba6b126824"))
            .append("rawjson", new BsonRegularExpression("", ""))
            .append("Annotatedlist", Arrays.asList("0", "1"))
            .append("Stoplist", Arrays.asList(""))
            .append("OriDes", 
            Arrays.asList("116th and Broadway, New York", "20 W 34th St., New York"))
            .append("RouteID", 0);
    String expectedResult = document.toJson();
    assertEquals(expectedResult, response.getBody());
  }

  @Test
  public void retrieveRouteTestEmptyOrigin() {
    ResponseEntity<?> response = testRc.retrieveRoute("", destinationExists);
    String expectedResult = "Invalid Inputs!";
    assertEquals(expectedResult, response.getBody());
  }

  @Test
  public void retrieveRouteTestEmptyDes() {
    ResponseEntity<?> response = testRc.retrieveRoute(originExists, "");
    String expectedResult = "Invalid Inputs!";
    assertEquals(expectedResult, response.getBody());
  }
  @Test
  public void retrieveRouteTestInvalidOrigin() {
    ResponseEntity<?> response = testRc.retrieveRoute("hello", destinationExists);
    String expectedResult = "Address not found.";
    assertEquals(expectedResult, response.getBody());
  }
  @Test
  public void retrieveRouteTestInvalidDes() {
    ResponseEntity<?> response = testRc.retrieveRoute(originExists, "hello");
    String expectedResult = "Address not found.";
    assertEquals(expectedResult, response.getBody());
  }

  @Test
  public void retrieveRouteNotExistsTest() {
    ResponseEntity<?> response = testRc.retrieveRoute(originNotExists, destinationNotExists);
    String expectedResult = "Successfully Created!";
    assertEquals(expectedResult, response.getBody());
  }

  @Test
  public void deleteDocumentTestFound() {
    testRc.retrieveRoute(originNotExists, destinationNotExists);
    ResponseEntity<?> response = testRc.deleteRoute(originNotExists, destinationNotExists);
    String expectedResult = "Successfully deleted";
    assertEquals(expectedResult, response.getBody());
  }

  @Test
  public void deleteDocumentTestNotFound() {
    String temporigin = "359-399 W 36th St., New York, NY 10018";
    String tempdestination = "425 W 33rd St, New York, NY 10001";
    ResponseEntity<?> response = testRc.deleteRoute(temporigin, tempdestination);
    String expectedResult = "Failed";
    assertEquals(expectedResult, response.getBody());
  }

  @Test
  public void computeRoutesTest() {
    Map<String, Object> entity = routeRequest.getRequestEntity();
    boolean googleResponse;
    googleResponse = testRc.computeRoutes(entity).getStatusCode() == HttpStatus.OK;
    boolean expectedResult = true;
    assertEquals(expectedResult, googleResponse);
  }

  /** The test course instance used for testing. */
  public static String originExists;
  public static String destinationExists;
  public static String originNotExists;
  public static String destinationNotExists;
  public static RouteRequestGoogle routeRequest;
  public static RouteController testRc;


}

