package app;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RestController;

/**
 * This class is annotation for routes.
 */
// @RestController
public class Route {

  public Route(int routeID, String rawjson, List<Map<String, Object>> OriDes, List<Map<String, Object>> StopList, List<Map<String, Object>> Annotatedlist) {
    this.routeID = routeID;
    this.rawjson = rawjson;
    this.OriDes = OriDes;
    this.StopList = StopList;
    this.Annotatedlist = Annotatedlist;
  }

  public int getRouteID() {
    return this.routeID;
  }

  public String getrawjson() {
    return this.rawjson;
  }

  public List<Map<String, Object>> getOriDes() {
    return this.OriDes;
  }

  public List<Map<String, Object>> getStopList() {
    return this.StopList;
  }

  public List<Map<String, Object>> getAnnotatedlist() {
    return this.Annotatedlist;
  }

  private int routeID;
  private String rawjson;
  private List<Map<String, Object>> OriDes;
  private List<Map<String, Object>> StopList;
  private List<Map<String, Object>> Annotatedlist;
}