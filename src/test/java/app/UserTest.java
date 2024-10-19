package app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class UserTest {

    @Test
    public void testGettersAndSetters() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("johndoe@example.com");
        user.setPassword("password123");
        user.setPreferences("User preferences");
        user.setGeoLocation("New York");

        assertEquals("John Doe", user.getName());
        assertEquals("johndoe@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("User preferences", user.getPreferences());
        assertEquals("New York", user.getGeoLocation());
    }
}
