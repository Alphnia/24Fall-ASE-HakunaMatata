package app;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Set;

// import org.hibernate.mapping.Set;

/**
* ReadJson function.
*
* 
*/
public class ReadJson {

  public ReadJson(String jsonString) {
    this.jsonString = jsonString;
  }

  /**
   * GetContent function.
   */
  public String[] getContent() {
    String[] stepArray = new String[0];
    try {
      JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
      JsonArray routes = jsonObject.getAsJsonArray("routes");
      ArrayList<String> legList = new ArrayList<>();

      // Process only the first route
      if (routes.size() > 0) {
        JsonArray legs = routes.get(0).getAsJsonObject().get("legs").getAsJsonArray();

        // Process only the first leg
        if (legs.size() > 0) {
          JsonArray steps = legs.get(0).getAsJsonObject().get("steps").getAsJsonArray();
          JsonArray extractedSteps = new JsonArray();

          for (int s = 0; s < steps.size(); s++) {
            JsonObject eachStep = steps.get(s).getAsJsonObject();
            if (eachStep.has("transitDetails")) {
              JsonObject transitDetails = eachStep.getAsJsonObject("transitDetails");
              JsonObject getTransits = new JsonObject();

              // Extract stop details
              JsonObject stopDetails = transitDetails.getAsJsonObject("stopDetails");
              JsonObject extractedStops = new JsonObject();
              extractedStops.add("arrivalStop", stopDetails.getAsJsonObject("arrivalStop"));
              extractedStops.add("departureStop", stopDetails.getAsJsonObject("departureStop"));
              getTransits.add("stopDetails", extractedStops);

              // Add headsign and headway
              if (transitDetails.has("headsign")) {
                getTransits.addProperty("headsign", transitDetails.get("headsign").getAsString());
              }
              if (transitDetails.has("headway")) {
                getTransits.addProperty("headway", transitDetails.get("headway").getAsString());
              }

              // Extract transit line details
              JsonObject transitLine = transitDetails.getAsJsonObject("transitLine");
              JsonObject getTransitLine = new JsonObject();
              if (transitLine.has("agencies")) {
                JsonObject data1 = transitLine.getAsJsonArray("agencies").get(0).getAsJsonObject();
                String data = data1.get("name").getAsString();
                getTransitLine.addProperty("Agencies", data);
              }
              getTransitLine.addProperty("name", transitLine.get("name").getAsString());
              getTransitLine.addProperty("color", transitLine.get("color").getAsString());
              getTransitLine.addProperty("nameShort", transitLine.get("nameShort").getAsString());
              getTransitLine.addProperty("textColor", transitLine.get("textColor").getAsString());
              String data2 = transitLine.getAsJsonObject("vehicle").get("type").getAsString();
              getTransitLine.addProperty("type", data2);
              getTransits.add("transitLine", getTransitLine);

              // Add stop count
              if (transitDetails.has("stopCount")) {
                getTransits.addProperty("stopCount", transitDetails.get("stopCount").getAsInt());
              }

              JsonObject extractedStep = new JsonObject();
              extractedStep.add("transitDetails", getTransits);
              extractedSteps.add(extractedStep);
            }
          }
          JsonObject extractedLeg = new JsonObject();
          extractedLeg.add("steps", extractedSteps);

          JsonObject extractedRoute = new JsonObject();
          extractedRoute.add("legs", new JsonArray());
          extractedRoute.getAsJsonArray("legs").add(extractedLeg);

          legList.add(extractedRoute.toString());
        }
      }

      stepArray = new String[legList.size()];
      legList.toArray(stepArray);
      return stepArray;
    } catch (Exception e) {
      e.printStackTrace();
      return stepArray;
    }
  }




  private String jsonString;
}
