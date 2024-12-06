package app;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


/**
 * this class deal with RESTful api requests about User Profile
 */

@RestController
@RequestMapping("/RealTime")
public class RealTimeController {
  
  // 

  @GetMapping("/startShareLocation")
  public ResponseEntity<?> startShareLocation(@RequestParam("UserID") String UserID,
    @RequestParam("AnnoID") String AnnoID){
      try {
        return new ResponseEntity<>(HttpStatus.OK);
      } catch (Exception e) {
        return handleException(e);
      }
  }
  
  @PutMapping("/update_location")
  public ResponseEntity<?> updateLocation(@RequestParam("latitude") Double latitude,
  @RequestParam("longitude") Double longitude,
  @RequestParam("annoId") String annoId){
      try {
        DatabaseOperation database = new DatabaseOperation("Annotation");
        String userId = database.getUserIdByAnnoId(Integer.parseInt(annoId));
        if (userId == null) {
          return new ResponseEntity<>("Failed to update location", HttpStatus.BAD_REQUEST);
        }
        Instant.now();
        DateTimeFormatter formatter = DateTimeFormatter
              .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
              .withZone(ZoneId.of("UTC"));
        DatabaseOperation database_track = new DatabaseOperation("track_location");
        database_track.createDocument_track(userId, latitude, longitude, formatter);

        return new ResponseEntity<>("Successfully updated",HttpStatus.OK);
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
