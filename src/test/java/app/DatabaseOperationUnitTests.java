package app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Database operation for annotation.
 */
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

  /**
   * Sets up the mocks and initializes the DatabaseOperation instance before each test.
   * This method ensures that the necessary mocks for MongoDatabase and MongoCollection
   * are properly initialized and linked to the test instance.
   */
  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    when(mockDatabase.getCollection("Route")).thenReturn(mockRouteCollection);
    when(mockDatabase.getCollection("Annotation")).thenReturn(mockAnnotationCollection);
    dbOperation = new DatabaseOperation(true, "1", "user1");
  }


  /**
   * Tests the scenario where no route is found for the given route Id.
   * Mocks the find operation for the Route collection, returning null, 
   * and verifies that the method returns null when no matching route is found.
   */
  @Test
  public void testFindRouteByIdsNotFound() {
    when(mockRouteCollection.find((Bson) any())).thenReturn(mockIterable);
    when(mockIterable.first()).thenReturn(null);

    String result = dbOperation.findRoutebyIds("1");
    assertNotNull(result);
  }

  /**
   * Tests the scenario where no annotation is found for the given route Id and user Id.
   * Mocks the find operation for the Annotation collection, returning null, 
   * and verifies that the method returns null when no matching annotation is found.
   */
  @Test
  public void testFindAnnotationByIdsNotFound() {
    when(mockAnnotationCollection.find((Bson) any())).thenReturn(mockIterable);
    when(mockIterable.first()).thenReturn(null);

    String result = dbOperation.findAnnotationbyIds("1", "34242");
    assertNull(result);
  }

  /**
   * Tests the successful update of an annotation.
   * Mocks the updateOne operation for the Annotation collection and ensures that 
   * the update is acknowledged. Verifies that the method returns "Update success".
   */
  @Test
  public void testUpdateAnnoSuccess() {
    UpdateResult updateResult = mock(UpdateResult.class);
    when(updateResult.wasAcknowledged()).thenReturn(true);
    when(mockAnnotationCollection.updateOne((Bson) any(), (Bson) any())).thenReturn(updateResult);

    String result = dbOperation.updateAnno("1", "user1", new ArrayList<>());
    assertEquals("Update success", result);
  }

  /**
   * Tests the successful insertion of an annotation.
   * Mocks the countDocuments and insertOne operations for the Annotation collection, 
   * and verifies that the method returns "Insert success" after inserting a new annotation.
   */
  @Test
  public void testInsertAnnoSuccess() {
    when(mockAnnotationCollection.countDocuments()).thenReturn(1L);
    when(mockAnnotationCollection.insertOne(any())).thenReturn(null);

    String result = dbOperation.insertAnno("1", "user1", new ArrayList<>());
    assertEquals("Insert success", result);
  }

  /**
   * Tests the successful deletion of an annotation.
   * Mocks the deleteOne operation for the Annotation collection and ensures 
   * that the correct document is deleted. Verifies that the method returns "Delete success".
   */
  @Test
  public void testDeleteAnnoSuccess() {
    DeleteResult deleteResult = mock(DeleteResult.class);
    when(deleteResult.getDeletedCount()).thenReturn(1L);
    when(mockAnnotationCollection.deleteOne((Bson) any())).thenReturn(deleteResult);

    String result = dbOperation.deleteAnno("1", "user1");
    assertEquals("Delete success", result);
  }

  /**
   * Tests the scenario where the annotation to be deleted is not found.
   * Mocks the deleteOne operation for the Annotation collection, returning no deletions, 
   * and verifies that the method returns "Annotation not found" when no annotation is deleted.
   */
  @Test
  public void testDeleteAnnoNotFound() {
    DeleteResult deleteResult = mock(DeleteResult.class);
    when(deleteResult.getDeletedCount()).thenReturn(0L);
    when(mockAnnotationCollection.deleteOne((Bson) any())).thenReturn(deleteResult);

    String result = dbOperation.deleteAnno("1", "user1");
    assertEquals("Annotation not found", result);
  }
}
