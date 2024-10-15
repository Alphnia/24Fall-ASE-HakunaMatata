package app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    String[] stopList = new String[0];
    try {
      JsonArray routes = JsonParser.parseString(jsonString).getAsJsonObject().getAsJsonArray("routes");
      JsonArray legs = routes.get(0).getAsJsonObject().get("legs").getAsJsonArray();
      JsonArray steps = legs.get(0).getAsJsonObject().get("steps").getAsJsonArray();
      stopList = new String[steps.size()];
      for (int i = 0; i < steps.size(); i++) {
        // String jsonObject = steps.get(i).getAsJsonObject().getAsString();
        JsonObject x = steps.get(i).getAsJsonObject();
        String y = x.getAsString();
        System.out.println(y);
        // stopList[i] = jsonObject;
        // // Loop through the keys in each object
        // for (String key : jsonObject.keySet()) {
        //   System.out.println("Key: " + key + ", Value: " + jsonObject.get(key).getAsString());
        // }
      }
      return stopList;

    } catch (Exception e) {
      e.printStackTrace();
      return stopList;
    }
  }
  private String jsonString;
}
