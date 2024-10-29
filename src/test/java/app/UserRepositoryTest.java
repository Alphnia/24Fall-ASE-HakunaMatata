package app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataMongoTest
public class UserRepositoryTest {

    @BeforeEach 
    public void cleanDatabase() {
        userRepository.deleteAll();
    }


    @Autowired
    private UserRepository userRepository;

    // Remove the user with the specified email if it exists
    private void removeExistingUserByEmail(String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        existingUser.ifPresent(user -> userRepository.deleteById(user.getId()));
    }

    @Test
    public void testSaveAndFindById() {
        // Create a new user
        User user = new User("John Doe", "john.doe@example.com", "password123", "{\"language\":\"en\"}", "{\"lat\":37.7749,\"lng\":-122.4194}");

        // Remove existing user if it exists
        removeExistingUserByEmail(user.getEmail());

        // Save the user
        User savedUser = userRepository.save(user);

        // Find the user by ID
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // Assert that the user was saved and found correctly
        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getName(), foundUser.get().getName());
    }

    @Test
    public void testFindByName() {
        // Create a new user
        User user = new User("Alice Smith", "alice@example.com", "hashed_password", "{\"language\":\"en\"}", "{\"lat\":40.7128,\"lng\":-74.0060}");

        // Remove existing user if it exists
        removeExistingUserByEmail(user.getEmail());

        // Save the user
        User savedUser = userRepository.save(user);

        // Find the user by name
        Optional<User> foundUser = userRepository.findByName("Alice Smith");

        // Assert that the user was found correctly
        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getName(), foundUser.get().getName());
    }

    @Test
    public void testFindByEmail() {
        // Create a new user
        User user = new User("Bob Johnson", "bob@example.com", "hashed_password", "{\"language\":\"en\"}", "{\"lat\":34.0522,\"lng\":-118.2437}");

        // Remove existing user if it exists
        removeExistingUserByEmail(user.getEmail());

        // Save the user
        User savedUser = userRepository.save(user);

        // Find the user by email
        Optional<User> foundUser = userRepository.findByEmail("bob@example.com");

        // Assert that the user was found correctly
        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getEmail(), foundUser.get().getEmail());
    }
}
