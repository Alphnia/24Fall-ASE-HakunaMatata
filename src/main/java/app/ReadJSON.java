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
  *
  * 
  */ 
  // public String[] getContent() {
  //   String[] stepArray = new String[0];
  //   try {
  //     JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
  //     // Set<String> responseKeys = jsonObject.keySet();
  //     // String getKey = responseKeys.iterator().next();
  //     JsonArray routes = jsonObject.getAsJsonArray("routes");
  //     JsonArray legs = routes.get(0).getAsJsonObject().get("legs").getAsJsonArray();
  //     JsonArray steps = legs.get(0).getAsJsonObject().get("steps").getAsJsonArray();
  //     ArrayList<String> stepList = new ArrayList<>();

  //     for (int i = 0; i < steps.size(); i++) {
  //       JsonObject eachStep = steps.get(i).getAsJsonObject();

  //       if (eachStep.size() != 0) {
  //         String getJsonString = eachStep.toString();
  //         stepList.add(getJsonString);
  //       }
  //       // // Loop through the keys in each object
  //       // for (String key : jsonObject.keySet()) {
  //       //   System.out.println("Key: " + key + ", Value: " + jsonObject.get(key).getAsString());
  //       // }
  //     }
  //     stepArray = new String[stepList.size()];
  //     stepList.toArray(stepArray);
  //     return stepArray;
  //   } catch (Exception e) {
  //     e.printStackTrace();
  //     return stepArray;
  //   }
  // }
  public String[] getContent() {
    String[] stepArray = new String[0];
    try {
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        JsonArray routes = jsonObject.getAsJsonArray("routes");
        ArrayList<String> legList = new ArrayList<>();

        for (int r = 0; r < routes.size(); r++) {
            JsonArray legs = routes.get(r).getAsJsonObject().get("legs").getAsJsonArray();
            JsonArray extractedLegs = new JsonArray();

            for (int l = 0; l < legs.size(); l++) {
                JsonArray steps = legs.get(l).getAsJsonObject().get("steps").getAsJsonArray();
                JsonArray extractedSteps = new JsonArray();

                for (int s = 0; s < steps.size(); s++) {
                    JsonObject eachStep = steps.get(s).getAsJsonObject();
                    if (eachStep.has("transitDetails")) {
                        JsonObject transitDetails = eachStep.getAsJsonObject("transitDetails");
                        JsonObject extractedTransitDetails = new JsonObject();

                        // Extract stop details
                        JsonObject stopDetails = transitDetails.getAsJsonObject("stopDetails");
                        JsonObject extractedStopDetails = new JsonObject();
                        extractedStopDetails.add("arrivalStop", stopDetails.getAsJsonObject("arrivalStop"));
                        extractedStopDetails.add("departureStop", stopDetails.getAsJsonObject("departureStop"));
                        extractedTransitDetails.add("stopDetails", extractedStopDetails);

                        // Add headsign and headway
                        if (transitDetails.has("headsign")) {
                            extractedTransitDetails.addProperty("headsign", transitDetails.get("headsign").getAsString());
                        }
                        if (transitDetails.has("headway")) {
                            extractedTransitDetails.addProperty("headway", transitDetails.get("headway").getAsString());
                        }

                        // Extract transit line details
                        JsonObject transitLine = transitDetails.getAsJsonObject("transitLine");
                        JsonObject extractedTransitLine = new JsonObject();
                        if (transitLine.has("agencies")) {
                            extractedTransitLine.addProperty("Agencies",
                                    transitLine.getAsJsonArray("agencies").get(0).getAsJsonObject().get("name").getAsString());
                        }
                        extractedTransitLine.addProperty("name", transitLine.get("name").getAsString());
                        extractedTransitLine.addProperty("color", transitLine.get("color").getAsString());
                        extractedTransitLine.addProperty("nameShort", transitLine.get("nameShort").getAsString());
                        extractedTransitLine.addProperty("textColor", transitLine.get("textColor").getAsString());
                        extractedTransitLine.addProperty("type", transitLine.getAsJsonObject("vehicle").get("type").getAsString());
                        extractedTransitDetails.add("transitLine", extractedTransitLine);

                        // Add stop count
                        if (transitDetails.has("stopCount")) {
                            extractedTransitDetails.addProperty("stopCount", transitDetails.get("stopCount").getAsInt());
                        }

                        JsonObject extractedStep = new JsonObject();
                        extractedStep.add("transitDetails", extractedTransitDetails);
                        extractedSteps.add(extractedStep);
                    }
                }

                JsonObject extractedLeg = new JsonObject();
                extractedLeg.add("steps", extractedSteps);
                extractedLegs.add(extractedLeg);
            }

            JsonObject extractedRoute = new JsonObject();
            extractedRoute.add("legs", extractedLegs);
            legList.add(extractedRoute.toString());
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
