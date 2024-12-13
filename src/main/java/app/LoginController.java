package app;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * LoginController handles user authentication operations such as login and registration.
 */
@RestController
public class LoginController {

  @Autowired
  private UserRepository userRepository;

  /**
   * Login endpoint.
   *
   * @param loginRequest A {@code LoginRequest} object containing email and password.
   * @return If authentication is successful, returns 200 status; otherwise, returns 400 or 401 status.
   */
  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
    String email = loginRequest.getEmail();
    String password = loginRequest.getPassword();

    Optional<User> userOpt = userRepository.findByEmail(email);
    if (userOpt.isPresent()) {
      User user = userOpt.get();
      if (user.getPassword().equals(password)) { // Simplified: directly compare plaintext passwords
        return new ResponseEntity<>("Login successful", HttpStatus.OK);
      } else {
        return new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
      }
    } else {
      return new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
    }
  }

  /**
   * Registration endpoint.
   *
   * @param registerRequest A {@code RegisterRequest} object containing user information.
   * @return If registration is successful, returns 200 status; if email already exists, returns 400 status.
   */
  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
    String email = registerRequest.getEmail();

    Optional<User> existingUser = userRepository.findByEmail(email);
    if (existingUser.isPresent()) {
      return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
    }

    User newUser = new User(
        registerRequest.getName(),
        email,
        registerRequest.getPassword(),
        registerRequest.getPreferences(),
        registerRequest.getGeoLocation()
    );

    userRepository.save(newUser);
    return new ResponseEntity<>("Registration successful", HttpStatus.OK);
  }

  /**
   * DTO class for login requests.
   */
  public static class LoginRequest {
    private String email;
    private String password;

    public LoginRequest() {}

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
  }

  /**
   * DTO class for registration requests.
   */
  public static class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private String preferences;
    private String geoLocation;

    public RegisterRequest() {}

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
}