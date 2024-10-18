package app;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class is annotation for routes.
 */
// @RestController
public class Route {
  /**
  * Route function.
  *
  * 
  */
  public Route(int routeId, String rawjson, List<Map<String, Object>> orides,
      List<Map<String, Object>> stoplist, List<Map<String, Object>> annotatedlist) {
    this.routeId = routeId;
    this.rawjson = rawjson;
    this.orides = orides;
    this.stoplist = stoplist;
    this.annotatedlist = annotatedlist;
  }

  public int getrouteId() {
    return this.routeId;
  }

  public String getrawjson() {
    return this.rawjson;
  }

  public List<Map<String, Object>> getorides() {
    return this.orides;
  }

  public List<Map<String, Object>> getstoplist() {
    return this.stoplist;
  }

  public List<Map<String, Object>> getAnnotatedlist() {
    return this.annotatedlist;
  }

  private int routeId;
  private String rawjson;
  private List<Map<String, Object>> orides;
  private List<Map<String, Object>> stoplist;
  private List<Map<String, Object>> annotatedlist;
}