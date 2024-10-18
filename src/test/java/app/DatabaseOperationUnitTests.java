package app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import org.mockito.stubbing.Answer;

public class DatabaseOperationUnitTests {

  @Mock
  private MongoDatabase mockDatabase;

  @Mock
  private MongoCollection<Document> mockRouteCollection;

  @Mock
  private MongoCollection<Document> mockAnnotationCollection;

  @Mock
  private FindIterable<Document> mockIterable;

  @InjectMocks
  private DatabaseOperation dbOperation;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    when(mockDatabase.getCollection("Route")).thenReturn(mockRouteCollection);
    when(mockDatabase.getCollection("Annotation")).thenReturn(mockAnnotationCollection);
    dbOperation = new DatabaseOperation(true, "1", "user1");
  }

  @Test
  public void testFindRouteByIDsFound() {
    Document mockDocument = new Document("RouteID", "1").append("key", "value");

    when(mockRouteCollection.find(any(Document.class))).thenReturn(mockIterable);
    when(mockIterable.first()).thenReturn(mockDocument);

    String result = dbOperation.FindRoutebyIDs("1");
    assertNotNull(result);
    assertTrue(result.contains("\"RouteID\":\"1\""));
  }

  @Test
  public void testFindAnnotationByIDsFound() {
    Document mockDocument = new Document("IDs", Arrays.asList("1", "user1"));

    when(mockAnnotationCollection.find(any(Document.class))).thenReturn(mockIterable);
    when(mockIterable.first()).thenReturn(mockDocument);

    String result = dbOperation.FindAnnotationbyIDs("1", "user1");
    assertNotNull(result);
    assertTrue(result.contains("\"IDs\":[\"1\",\"user1\"]"));
  }

  @Test
  public void testFindRouteByIDsNotFound() {
    when(mockRouteCollection.find(any(Document.class))).thenReturn(mockIterable);
    when(mockIterable.first()).thenReturn(null);

    String result = dbOperation.FindRoutebyIDs("1");
    assertNull(result);
  }

  @Test
  public void testFindAnnotationByIDsNotFound() {
    when(mockAnnotationCollection.find(any(Document.class))).thenReturn(mockIterable);
    when(mockIterable.first()).thenReturn(null);

    String result = dbOperation.FindAnnotationbyIDs("1", "user1");
    assertNull(result);
  }

  @Test
  public void testUpdateAnnoSuccess() {
    String result = dbOperation.UpdateAnno("1", "user1", new ArrayList<>());
    assertEquals("Update success", result);
  }

  @Test
  public void testInsertAnnoSuccess() {
    when(mockAnnotationCollection.countDocuments()).thenReturn(1L);

    String result = dbOperation.InsertAnno("1", "user1", new ArrayList<>());
    assertEquals("Insert success", result);
  }

  @Test
  public void testDeleteAnnoSuccess() {
    DeleteResult deleteResult = mock(DeleteResult.class);
    when(deleteResult.getDeletedCount()).thenReturn(1L);
    when(mockAnnotationCollection.deleteOne(any(Document.class))).thenReturn(deleteResult);

    String result = dbOperation.DeleteAnno("1", "user1");
    assertEquals("Delete success", result);
  }

  @Test
  public void testDeleteAnnoNotFound() {
    DeleteResult deleteResult = mock(DeleteResult.class);
    when(deleteResult.getDeletedCount()).thenReturn(0L);
    when(mockAnnotationCollection.deleteOne(any(Document.class))).thenReturn(deleteResult);

    String result = dbOperation.DeleteAnno("1", "user1");
    assertEquals("Annotation not found", result);
  }
}