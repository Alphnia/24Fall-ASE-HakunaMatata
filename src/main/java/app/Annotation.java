package app;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RestController;

/**
 * This class is annotation for routes.
 */
// @RestController
public class Annotation {

  public Annotation(int annoID, int routeID, int userID, List<Map<String, Object> > StopList){
    this.annoID = annoID;
    this.routeID = routeID;
    this.userID = userID;
    this.StopList = StopList;
  }
  public int getAnnoID(){
    return this.annoID;
  }

  public int getRouteID(){
    return this.routeID;
  }
  
  public int getUserID(){
    return this.userID;
  }
  public List<Map<String, Object>> getStopList (){
    return this.StopList;
  }
  
  private int annoID;
  private int routeID;
  private int userID;
  private List<Map<String, Object>> StopList; 
}