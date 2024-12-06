package app;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * User Profile class.
 */

@Document(collection = "User_profile")
public class User {
  @Id
  private ObjectId id; // MongoDB autogen id
  private String name;
  private String email;
  private String password;
  private String preferences; 
  private String geoLocation; 

  public User() {}

  /**
   * User construct fuction.
   */
  public User(String name, String email, String password, String preferences, String geoLocation) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.preferences = preferences;
    this.geoLocation = geoLocation;
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPreferences() {
    return preferences;
  }

  public void setPreferences(String preferences) {
    this.preferences = preferences;
  }

  public String getGeoLocation() {
    return geoLocation;
  }

  public void setGeoLocation(String geoLocation) {
    this.geoLocation = geoLocation;
  }
}
