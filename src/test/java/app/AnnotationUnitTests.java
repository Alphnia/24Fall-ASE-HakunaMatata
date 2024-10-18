package app;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AnnotationUnitTests {

  private Annotation annotation;
  private List<Map<String, Object>> stopList;

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

  @Test
  public void testGetAnnoID() {
    assertEquals(1, annotation.getAnnoID());
  }

  @Test
  public void testGetRouteID() {
    assertEquals(101, annotation.getRouteID());
  }

  @Test
  public void testGetUserID() {
    assertEquals(1001, annotation.getUserID());
  }

  @Test
  public void testGetStopList() {
    List<Map<String, Object>> result = annotation.getStopList();
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("turn right at the corner", result.get(0).get("direction"));
    assertEquals("go straight until the light", result.get(1).get("direction"));
  }

  @Test
  public void testEmptyConstructor() {
    Annotation emptyAnnotation = new Annotation();
    assertEquals(0, emptyAnnotation.getAnnoID());
    assertEquals(0, emptyAnnotation.getRouteID());
    assertEquals(0, emptyAnnotation.getUserID());
    assertNull(emptyAnnotation.getStopList());
  }
}
