package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RouteRequestGoogle.
 * 
 */
public class RouteRequestGoogle {

  /**
   * RouteRequestGoogle initialization.
   * 
   */
  public RouteRequestGoogle(String origin, String destination) {
    this.origin.put("address", origin);
    this.destination.put("address", destination);
    this.travelMode = "TRANSIT";
    this.computeAlternativeRoutes = true;
    List<String> allowedTravelModes = new ArrayList<>();
    this.transitPreferences.put("routingPreference", "FEWER_TRANSFERS");
    allowedTravelModes.add("SUBWAY");
    this.transitPreferences.put("allowedTravelModes", allowedTravelModes);
  }

  /**
  * Retrieves a map containing the request entity details.
  *
  * <p>
  * This method constructs and returns a {@code Map} with keys representing
  * various attributes related to the request, including origin, destination,
  * travel mode, alternative route computation preference, and transit preferences.
  * </p>
  *
  * @return A {@code Map<String, Object>} containing the request entity details.
  */
  public Map<String, Object> getRequestEntity() {
    Map<String, Object> entity = new HashMap<>();
    entity.put("origin", this.origin);
    entity.put("destination", this.destination);
    entity.put("travelMode", this.travelMode);
    entity.put("computeAlternativeRoutes", this.computeAlternativeRoutes);
    entity.put("transitPreferences", this.transitPreferences);

    return entity;
  }

  private Map<String, String> origin = new HashMap<>();
  private Map<String, String> destination = new HashMap<>();
  private String travelMode;
  private Boolean computeAlternativeRoutes;
  private Map<String, Object> transitPreferences = new HashMap<>();

}
