package app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    Map<String, String> mp = new HashMap<String, String>();
    mp.put("direction", "go straight until you see a 711.");
    Map<String, Object> node = new HashMap<String, Object>();
    node.put("1", mp);
    mp.clear();
    mp.put("direction", "turn right.");
    Map<String, Object> node2 = new HashMap<String, Object>();
    node2.put("2", mp);
    stopList = new ArrayList<>();
    stopList.add(node);
    stopList.add(node2);
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

  @Test
  public void testEditRoute_UpdateSuccess() {
    String routeId = "1";
    String userId = "670c4dab7013573300601f64";
    // 假设记录已存在
    ResponseEntity<?> response = testRc.editRoute(routeId, userId, stopList);
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void testEditRoute_UpdateFailure() {
    String routeId = "1";
    String userId = "670c4dab7013573300601f64";
    List<Map<String, Object>> emptyStoplList = new ArrayList<Map<String, Object>>();
    ResponseEntity<?> response = testRc.editRoute(routeId, userId, emptyStoplList);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  public void testEditRoute_InvalidInput() {
    String routeId = "1";
    String userId = "xy.z";
    ResponseEntity<?> response = testRc.editRoute(routeId, userId, stopList);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    routeId = "#@^&*";
    userId = "670c4dab7013573300601f64";
    response = testRc.editRoute(routeId, userId, stopList);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    routeId = null;
    userId = "670c4dab7013573300601f64";
    response = testRc.editRoute(routeId, userId, stopList);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    routeId = "2";
    userId = null;
    response = testRc.editRoute(routeId, userId, stopList);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  public void testcheckAnno_ValidInput()  {
    String routeId = "1";
    String userId = "1";
    ResponseEntity<?> response = testRc.checkAnnos(routeId, userId);
    // System.out.println(response.getBody());
    // assertEquals(HttpStatus.OK, response.getStatusCode());

    routeId = "5";
    userId = "670c4dab7013573300601f64";
    response = testRc.checkAnnos(routeId, userId);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void testcheckAnno_InvalidInput() {
    String routeId = null;
    String userId = "670c4dab7013573300601f64";
    ResponseEntity<?> response = testRc.checkAnnos(routeId, userId);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    routeId = "3";
    userId = null;
    response = testRc.checkAnnos(routeId, userId);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    routeId = "turieoutoe";
    userId = "670c4dab7013573300601f64";
    response = testRc.checkAnnos(routeId, userId);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    
    routeId = "3";
    userId = "#&%*(@)";
    response = testRc.checkAnnos(routeId, userId);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

  }

  @Test
  public void testdeleteAnno_ValidInput() {
    String routeId = "1";
    String userId = "670c4dab7013573300601f64";
    ResponseEntity<?> response = testRc.deleteAnnotation(routeId, userId);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    routeId = "5";
    userId = "670c4dab7013573300601f64";
    response = testRc.deleteAnnotation(routeId, userId);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void testdeleteAnno_InValidInput() {
    String routeId = "&*^*^%$";
    String userId = "670c4dab7013573300601f64";
    ResponseEntity<?> response = testRc.deleteAnnotation(routeId, userId);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    routeId = "3";
    userId = "&*^*^%$";
    response = testRc.deleteAnnotation(routeId, userId);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    routeId = null;
    userId = "670c4dab7013573300601f64";
    response = testRc.deleteAnnotation(routeId, userId);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    routeId = "2";
    userId = null;
    response = testRc.deleteAnnotation(routeId, userId);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      
  }

  /** The test course instance used for testing. */
  public static String originExists;
  public static String destinationExists;
  public static String originNotExists;
  public static String destinationNotExists;
  public static RouteRequestGoogle routeRequest;
  public static RouteController testRc;
  public static List<Map<String, Object>> stopList;

}


