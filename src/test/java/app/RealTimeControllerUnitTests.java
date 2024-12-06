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
public class RealTimeControllerUnitTests {

  /**
   * The setupRealTimeForTesting is used to set up instances for testing.
   *
   */
  @BeforeAll
  public static void setupRouteForTesting() {
    testRTC = new RealTimeController();
  }

  @Test
  public void updateLocationTest() {
    Double latitude = 40.762569;
    Double longitude = -73.975731;
    String annoId = "19";
    ResponseEntity<?> response = testRTC.updateLocation(latitude, longitude, annoId);
    String expectedResult = "Successfully updated";
    assertEquals(expectedResult, response.getBody());
  }

  @Test
  public void updateLocationTest_False() {
    Double latitude = 40.762569;
    Double longitude = -73.975731;
    String annoId = "100";
    ResponseEntity<?> response = testRTC.updateLocation(latitude, longitude, annoId);
    String expectedResult = "Failed to update location";
    assertEquals(expectedResult, response.getBody());
  }


  public static RealTimeController testRTC;
  
}
