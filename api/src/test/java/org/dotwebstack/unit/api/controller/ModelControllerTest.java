package org.dotwebstack.unit.api.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.dotwebstack.api.controller.ModelController;
import org.dotwebstack.data.client.TripleStoreClient;
import org.dotwebstack.test.categories.Categories;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Created by Rick Fleuren on 6/12/2017.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public class ModelControllerTest {

  @Mock
  TripleStoreClient client;

  public void init(ModelController controller) {
    ReflectionTestUtils.setField(controller, "defaultNamespace", "default/");
  }

  @Test
  public void testModels() {
    //arrange
    LinkedHashModel model = new LinkedHashModel();
    when(client.query()).thenReturn(model);

    ModelController controller = new ModelController(client);
    init(controller);

    //act
    ResponseEntity<Model> result = controller.getModels();

    //assert
    assertEquals("Models should be the same", model, result.getBody());
    assertEquals("Status code should be 200", HttpStatus.OK, result.getStatusCode());
  }

  @Test
  public void testModelBySubjectDefaultNamespace() {
    //arrange
    LinkedHashModel model = new LinkedHashModel();
    when(client.queryBySubject(eq("default/subject"))).thenReturn(model);
    ModelController controller = new ModelController(client);
    init(controller);

    //act
    ResponseEntity<Model> result = controller.getModel("subject", null);

    //assert
    assertEquals("Models should be the same", model, result.getBody());
    assertEquals("Status code should be 200", HttpStatus.OK, result.getStatusCode());
  }

  @Test
  public void testModelBySubject() {
    //arrange
    LinkedHashModel model = new LinkedHashModel();
    when(client.queryBySubject(eq("myNamespace/subject"))).thenReturn(model);
    ModelController controller = new ModelController(client);
    init(controller);

    //act
    ResponseEntity<Model> result = controller.getModel("subject", "myNamespace/");

    //assert
    assertEquals("Models should be the same", model, result.getBody());
    assertEquals("Status code should be 200", HttpStatus.OK, result.getStatusCode());
  }

  @Test
  public void testDeleteBySubjectDefaultNamespace() {
    //arrange
    ModelController controller = new ModelController(client);
    init(controller);

    //act
    controller.deleteModel("subject", null);

    //assert
    verify(client).deleteBySubject("default/subject");
  }

  @Test
  public void testDeleteBySubject() {
    //arrange
    ModelController controller = new ModelController(client);
    init(controller);

    //act
    controller.deleteModel("subject", "myNamespace/");

    //assert
    verify(client).deleteBySubject("myNamespace/subject");
  }
}
