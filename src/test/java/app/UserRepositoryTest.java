package app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SpringExtension.class)
@DataMongoTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveAndFindById() {
        // Create a new user
        User user = new User("John Doe", "john.doe@example.com", "password123", "{\"language\":\"en\"}", "{\"lat\":37.7749,\"lng\":-122.4194}");
        
        // Save the user
        userRepository.save(user);

        // Find the user by ID
        Optional<User> foundUser = userRepository.findById(user.getId());

        // Assert the user was saved and found correctly
        assertTrue(foundUser.isPresent());
        assertEquals(user.getName(), foundUser.get().getName());
    }

    @Test
    public void testFindByName() {
        // Create and save a user
        User user = new User("Alice Smith", "alice@example.com", "hashed_password", "{\"language\":\"en\"}", "{\"lat\":40.7128,\"lng\":-74.0060}");
        userRepository.save(user);

        // Find the user by name
        Optional<User> foundUser = userRepository.findByName("Alice Smith");

        // Assert the user was found correctly
        assertTrue(foundUser.isPresent());
        assertEquals(user.getName(), foundUser.get().getName());
    }

    @Test
    public void testFindByEmail() {
        // Create and save a user
        User user = new User("Bob Johnson", "bob@example.com", "hashed_password", "{\"language\":\"en\"}", "{\"lat\":34.0522,\"lng\":-118.2437}");
        userRepository.save(user);

        // Find the user by email
        Optional<User> foundUser = userRepository.findByEmail("bob@example.com");

        // Assert the user was found correctly
        assertTrue(foundUser.isPresent());
        assertEquals(user.getEmail(), foundUser.get().getEmail());
    }
}
