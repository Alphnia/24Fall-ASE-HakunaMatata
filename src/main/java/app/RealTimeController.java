package app;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;


/**
 * this class deal with RESTful api requests about Real time location sharing.
 */

@RestController
@RequestMapping("/RealTime")
public class RealTimeController {

  /**
   * Update location returned from the client side.
   *
   * @param location The location of the User sharing location.
   * 
   * @param annoId The annotation Id of the route User shared.
   * 
   * @return A {@code ResponseEntity} with an error message and NOT_FOUND status.
   *         A {@code ResponseEntity} with success message and 200 status.
   */
  @PutMapping("/update_location")
  public ResponseEntity<?> updateLocation(@RequestBody Location location,
      @RequestParam("annoId") String annoId) {
    try {
      Double latitude = location.getLatitude();
      Double longitude = location.getLongitude();
      DatabaseOperation database = new DatabaseOperation("Annotation");
      String userId = database.getUserIdByAnnoId(Integer.parseInt(annoId));
      if (userId == null) {
        return new ResponseEntity<>("Failed to update location", HttpStatus.BAD_REQUEST);
      }
      Instant.now();
      OffsetDateTime currentTime = OffsetDateTime.now();
      String formattedTime = currentTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
      DatabaseOperation databasetrack = new DatabaseOperation("track_location");
      ResponseEntity<?> response = databasetrack.createDocument_track(userId, 
          latitude, longitude, formattedTime);
      if (response.getStatusCode() == HttpStatus.OK) {
        return new ResponseEntity<>("Successfully updated", HttpStatus.OK);
      } else {
        return new ResponseEntity<>("Failed to insert database", HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      return handleException(e);
    }
  }

  /**
   * A link another user can click to track this user's location.
   *
   * @param userId The user Id of the User who starts sharing route.
   * 
   * @return A {@code ResponseEntity} with an error message and NOT_FOUND status.
   *         A {@code ResponseEntity} with location data and 200 status.
   */
  @GetMapping("/trackLocation")
  public ResponseEntity<?> trackLocation(@RequestParam("userID") String userId) {
    try {
      // Retrieve the latest location and timestamp from the "track_location" collection
      DatabaseOperation tracking = new DatabaseOperation("track_location");
      Map<String, Object> locationData = tracking.getLatestLocation(userId);

      if (locationData == null) {
        String message = "No location data found for UserID: " + userId;
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
      }

      return new ResponseEntity<>(locationData, HttpStatus.OK);
    } catch (Exception e) {
      return handleException(e);
    }
  }

  /**
   * Handles exceptions and returns a standard NOT_FOUND response with an error message.
   *
   * @param e The exception that was thrown.
   * @return A {@code ResponseEntity} with an error message and NOT_FOUND status.
   */
  private ResponseEntity<?> handleException(Exception e) {
    System.out.println(e.toString());
    return new ResponseEntity<>("An Error has occurred", HttpStatus.NOT_FOUND);
  }
}
