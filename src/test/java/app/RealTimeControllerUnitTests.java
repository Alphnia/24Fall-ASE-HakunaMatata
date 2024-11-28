package app;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
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
    testTrackLocation = new RealTimeController();
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

  @Test
  public void trackLocationTest_True() {
    String userId = "67100af310601b256ff4b39a";
    ResponseEntity<?> response = testTrackLocation.trackLocation(userId);

    Map<String, Object> expectedResult = Map.of(
        "latitude", 34.0522,
        "longitude", -118.2437,
        "timestamp", "2024-11-25T12:34:56.789-05:00"
    );
    Map<String, Object> actualResult = (Map<String, Object>) response.getBody();
    assertEquals(
        expectedResult.get("latitude"),
        actualResult.get("latitude")
    );
    assertEquals(
        expectedResult.get("longitude"),
        actualResult.get("longitude")
    );
  }

  @Test
  public void trackLocationTest_False() {
    String userId = "670c4dab7013573300601f64";
    ResponseEntity<?> response = testTrackLocation.trackLocation(userId);
    String expectedResult = "No location data found for UserID: " + userId;
    assertEquals(expectedResult, response.getBody());
  }


  public static RealTimeController testRTC;
  public static RealTimeController testTrackLocation;
}
