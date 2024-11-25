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

import java.util.Optional;

/**
 * this class deal with RESTful api requests about User Profile
 */

@RestController
@RequestMapping("/RealTime")
public class RealTimeController {
  
  // 

  @GetMapping("/startShareLocation")
  public ResponseEntity<Void> startShareLocation(@RequestParam("UserID") String UserID,
    @RequestParam("AnnoID") String AnnoID){
      try {
        return 
      } catch (Exception e) {
        return handleException(e);
      }
  }
  
  @PutMapping("/update_location")
    public ResponseEntity<Void> updateLocation(@RequestParam("origin") String origin,
      @RequestParam("destination") String destination, @RequestParam("annoId") String annoId){
        try {
          DatabaseOperation database = new DatabaseOperation();
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
