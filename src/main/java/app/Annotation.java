package app;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class is annotation for routes.
 */
// @RestController
public class Annotation {
  /**
   * Returns the details of the specified route.
   *
   * 
   */
  public Annotation(int annoId, int routeId, int userId, List<Map<String, Object>> stopList) {
    this.annoId = annoId;
    this.annoId = annoId;
    this.userId = userId;
    this.stopList = stopList;
  }

  public int getAnnoId() {
    return this.annoId;
  }

  public int getRouteId() {
    return this.routeId;
  }
  
  public int getUserId() {
    return this.userId;
  }

  public List<Map<String, Object>> getStopList() {
    return this.stopList;
  }
  
  private int annoId;
  private int routeId;
  private int userId;
  private List<Map<String, Object>> stopList; 
}