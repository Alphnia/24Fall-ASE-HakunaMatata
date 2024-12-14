package app;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isPresent()) {
            String connectionString = 
                "mongodb+srv://test_user:coms4156@cluster4156.287dv.mongodb.net/"
                + "?retryWrites=true&w=majority&appName=Cluster4156&tsl=true";
            MongoClient mongoClient = MongoClients.create(connectionString);
            MongoDatabase database = mongoClient.getDatabase("Hkunamatata_DB");
            MongoCollection<Document> collection = database.getCollection("users");
            Document query = new Document("email", email);
            FindIterable<Document> results = collection.find(query).limit(1);
            MongoCursor<Document> cursor = results.iterator();
            ObjectId userid;
            if (cursor.hasNext()) {
                Document document = cursor.next();
                userid = document.getObjectId("_id");
                String userId = userid.toHexString();
                User user = userOpt.get();
                if (user.getPassword().equals(password)) { // Simplified: directly compare plaintext passwords
                    return new ResponseEntity<>( userId, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
                }
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
