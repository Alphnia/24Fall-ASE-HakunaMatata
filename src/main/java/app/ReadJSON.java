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
  public String[] getContent() {
    String[] stepArray = new String[0];
    try {
      JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
      // Set<String> responseKeys = jsonObject.keySet();
      // String getKey = responseKeys.iterator().next();
      JsonArray routes = jsonObject.getAsJsonArray("routes");
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
