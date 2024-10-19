package app;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;

public class UserProfileControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserProfileController userProfileController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(userProfileController).build();
    }

    @Test
    public void testCreateUser() throws Exception {
        User user = new User("John Doe", "john.doe@example.com", "password123", "{\"language\":\"en\"}", "{\"lat\":37.7749,\"lng\":-122.4194}");

        when(userRepository.save(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    public void testGetUserById() throws Exception {
        ObjectId userId = new ObjectId();
        User user = new User("John Doe", "john.doe@example.com", "password123", "{\"language\":\"en\"}", "{\"lat\":37.7749,\"lng\":-122.4194}");
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/" + userId.toHexString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }


    @Test
    public void testGetUserByEmail() throws Exception {
        User user = new User("Jane Doe", "jane.doe@example.com", "password123", "{\"language\":\"en\"}", "{\"lat\":40.7128,\"lng\":-74.0060}");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/email/jane.doe@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.email").value("jane.doe@example.com"));
                
    }


}

