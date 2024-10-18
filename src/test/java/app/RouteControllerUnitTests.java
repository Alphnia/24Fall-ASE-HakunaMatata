package app;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class RouteControllerUnitTests {

  private MockMvc mockMvc;

  @Mock
  private DatabaseOperation dbOperation;

  @InjectMocks
  private RouteController routeController;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(routeController).build();
  }

  @Test
  public void testInsertAnnoForTest() throws Exception {
    mockMvc.perform(get("/insertAnnoForTest"))
        .andExpect(status().isOk())
        .andExpect(content().string("Insert complete"));
  }

  @Test
  public void testCheckAnnosFound() throws Exception {
    when(dbOperation.FindRoutebyIDs("1")).thenReturn("{RouteID:1}");
    when(dbOperation.FindAnnotationbyIDs("1", "user1")).thenReturn("{AnnoID:1}");

    mockMvc.perform(get("/checkAnno")
            .param("routeID", "1")
            .param("userID", "user1"))
        .andExpect(status().isOk());
  }

  @Test
  public void testCheckAnnosNotFound() throws Exception {
    when(dbOperation.FindRoutebyIDs("1")).thenReturn(null);

    mockMvc.perform(get("/checkAnno")
            .param("routeID", "1")
            .param("userID", "user1"))
        .andExpect(status().isNotFound());
  }

  @Test
  public void testEditRouteUpdateSuccess() throws Exception {
    List<Map<String, Object>> stopList = new ArrayList<>();
    when(dbOperation.UpdateAnno("1", "user1", stopList)).thenReturn("Update success");

    mockMvc.perform(patch("/editRoute")
            .param("routeID", "1")
            .param("userID", "user1")
            .contentType(MediaType.APPLICATION_JSON)
            .content("[]"))
        .andExpect(status().isOk())
        .andExpect(content().string("Update success"));
  }

  @Test
  public void testEditRouteInsertSuccess() throws Exception {
    List<Map<String, Object>> stopList = new ArrayList<>();
    when(dbOperation.UpdateAnno("1", "user1", stopList)).thenReturn("An Error has occurred");
    when(dbOperation.InsertAnno("1", "user1", stopList)).thenReturn("Insert success");

    mockMvc.perform(patch("/editRoute")
            .param("routeID", "1")
            .param("userID", "user1")
            .contentType(MediaType.APPLICATION_JSON)
            .content("[]"))
        .andExpect(status().isOk())
        .andExpect(content().string("Insert success"));
  }

  @Test
  public void testDeleteAnnotationSuccess() throws Exception {
    when(dbOperation.DeleteAnno("1", "user1")).thenReturn("Delete success");

    mockMvc.perform(delete("/deleteAnno")
            .param("routeID", "1")
            .param("userID", "user1"))
        .andExpect(status().isOk())
        .andExpect(content().string("Delete success"));
  }

  @Test
  public void testDeleteAnnotationNotFound() throws Exception {
    when(dbOperation.DeleteAnno("1", "user1")).thenReturn("Annotation not found");

    mockMvc.perform(delete("/deleteAnno")
            .param("routeID", "1")
            .param("userID", "user1"))
        .andExpect(status().isNotFound())
        .andExpect(content().string("Annotation not found"));
  }

  @Test
  public void testDeleteAnnotationError() throws Exception {
    when(dbOperation.DeleteAnno("1", "user1")).thenReturn("An Error has occurred");

    mockMvc.perform(delete("/deleteAnno")
            .param("routeID", "1")
            .param("userID", "user1"))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("An Error has occurred"));
  }
}