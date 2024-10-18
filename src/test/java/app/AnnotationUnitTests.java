package app;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test Annotation.
 */
public class AnnotationUnitTests {

  private Annotation annotation;
  private List<Map<String, Object>> stopList;

  /**
   * Sets up test data before each test case.
   * Initializes a sample stopList and creates an Annotation instance 
   * with predefined Ids for testing.
   */
  @BeforeEach
  public void setUp() {
    // Initialize example stopList data
    Map<String, Object> stop1 = new HashMap<>();
    stop1.put("direction", "turn right at the corner");
    Map<String, Object> stop2 = new HashMap<>();
    stop2.put("direction", "go straight until the light");

    stopList = new ArrayList<>();
    stopList.add(stop1);
    stopList.add(stop2);

    // Create Annotation instance
    annotation = new Annotation(1, 101, 1001, stopList);
  }

  /**
   * Tests to get the AnnoId field.
   * Verifies that the correct AnnoId is returned from the Annotation instance.
   */
  @Test
  public void testGetAnnoId() {
    assertEquals(1, annotation.getAnnoId());
  }

  /**
   * Tests to get for the RouteId field.
   * Verifies that the correct RouteId is returned from the Annotation instance.
   */
  @Test
  public void testGetRouteId() {
    assertEquals(101, annotation.getRouteId());
  }

  /**
   * Tests to get the UserId field.
   * Verifies that the correct UserId is returned from the Annotation instance.
   */
  @Test
  public void testGetUserId() {
    assertEquals(1001, annotation.getUserId());
  }

  /**
   * Tests to get the stopList field.
   * Verifies that the stopList is not null, has the expected size, 
   * and contains the correct directions for each stop.
   */
  @Test
  public void testGetStopList() {
    List<Map<String, Object>> result = annotation.getStopList();
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("turn right at the corner", result.get(0).get("direction"));
    assertEquals("go straight until the light", result.get(1).get("direction"));
  }

  /**
   * Tests the behavior of the empty constructor of the Annotation class.
   * Ensures that the default values for the empty Annotation instance are set 
   * as expected (AnnoId, RouteId, and UserId as 0, stopList as null).
   */
  @Test
  public void testEmptyConstructor() {
    Annotation emptyAnnotation = new Annotation();
    assertEquals(0, emptyAnnotation.getAnnoId());
    assertEquals(0, emptyAnnotation.getRouteId());
    assertEquals(0, emptyAnnotation.getUserId());
    assertNull(emptyAnnotation.getStopList());
  }
}
