package app;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * This class is annotation for routes.
 */
@Component
public class Annotation {

  /**
   * construction.
   */
  public Annotation() {
    
  }

  /**
   * Always use this construction.
   */
  public Annotation(int annoId, int routeId, int userId, List<Map<String, Object>> stopList) {
    this.annoId = annoId;
    this.routeId = routeId;
    this.userId = userId;
    this.stopList = stopList;
  }

  /**
   * get Annotation Id.
   */
  public int getAnnoId() {
    return this.annoId;
  }

  /**
   * get Route Id.
   */
  public int getRouteId() {
    return this.routeId;
  }
  
  /**
   * get User Id.
   */
  public int getUserId() {
    return this.userId;
  }

  /**
   * get stop list.
   */
  public List<Map<String, Object>> getStopList() {
    return this.stopList;
  }
  
  private int annoId;
  private int routeId;
  private int userId;
  private List<Map<String, Object>> stopList; 
}