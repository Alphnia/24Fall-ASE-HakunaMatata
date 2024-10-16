package app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ReadJSON {

  public ReadJSON(String jsonString) {
    this.jsonString = jsonString;
  }

  public String[] getContent() {
    String[] stepArray = new String[0];
    try {
      JsonArray routes = JsonParser.parseString(jsonString).getAsJsonObject().getAsJsonArray("routes");
      JsonArray legs = routes.get(0).getAsJsonObject().get("legs").getAsJsonArray();
      JsonArray steps = legs.get(0).getAsJsonObject().get("steps").getAsJsonArray();
      ArrayList<String> stepList = new ArrayList<>();

      for (int i = 0; i < steps.size(); i++) {
        JsonObject eachStep = steps.get(i).getAsJsonObject();

        if (eachStep.size() != 0) {
          String getJsonString = eachStep.toString();
          stepList.add(getJsonString);
        }
        // // Loop through the keys in each object
        // for (String key : jsonObject.keySet()) {
        //   System.out.println("Key: " + key + ", Value: " + jsonObject.get(key).getAsString());
        // }
      }
      stepArray = new String[stepList.size()];
      stepList.toArray(stepArray);
      return stepArray;

    } catch (Exception e) {
      e.printStackTrace();
      return stepArray;
    }
  }
  private String jsonString;
}
