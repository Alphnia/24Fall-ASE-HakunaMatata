package app;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * this class deal with RESTful api requests about User Profile
 */

@RestController
@RequestMapping("/users")
public class UserProfileController {

    @Autowired
    private UserRepository userRepository;

    // POST /users -- create users
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    // GET /users/{id} -- get by id
    @GetMapping("/{id}")
    public User getUserById(@PathVariable ObjectId id) {
        return userRepository.findById(id).orElse(null);
    }

    // PUT /users/{id} -- update 
    @PutMapping("/{id}")
    public User updateUser(@PathVariable ObjectId id, @RequestBody User userDetails) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setName(userDetails.getName());
            user.setEmail(userDetails.getEmail());
            user.setPassword(userDetails.getPassword());
            user.setPreferences(userDetails.getPreferences());
            user.setGeoLocation(userDetails.getGeoLocation());
            return userRepository.save(user);
        }
        return null;
    }

    // DELETE /users/{id} - delete user
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable ObjectId id) {
        userRepository.deleteById(id);
    }

    // GET /users/name/{name} - get by name
    @GetMapping("/name/{name}")
    public Optional<User> getUserByName(String name) {
        Optional<User> user = userRepository.findByName(name);
        user.ifPresent(u -> System.out.println("User found: " + u.getName()));
        return user;
    }
    // GET /users/email/{email} - get by email
    @GetMapping("/email/{email}")
    public Optional<User> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userRepository.findByEmail(email);
        user.ifPresent(u -> System.out.println("User found: " + u.getEmail()));
        return user ;
    }
}
