package org.dotwebstack.unit.api.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.dotwebstack.api.controller.InformationProductController;
import org.dotwebstack.data.service.InformationProduct;
import org.dotwebstack.data.service.InformationProductService;
import org.dotwebstack.test.categories.Categories;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Created by Rick Fleuren on 6/19/2017.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public class InformationProductControllerTest {

  @Mock
  InformationProductService service;

  @Mock
  HttpServletRequest request;

  @Captor
  ArgumentCaptor<Map<String, String>> argumentCaptor;

  public void init(InformationProductController controller) {
    ReflectionTestUtils.setField(controller, "defaultNamespace", "default/");
  }

  @Test
  public void testInformationProducts() {
    //arrange
    ArrayList<InformationProduct> informationProducts = new ArrayList<>();
    when(service.getInformationProducts()).thenReturn(informationProducts);

    InformationProductController controller = new InformationProductController(service);
    init(controller);

    //act
    ResponseEntity<List<InformationProduct>> result = controller.getInformationProducts();

    //assert
    assertEquals("Models should be the same", informationProducts, result.getBody());
    assertEquals("Status code should be 200", HttpStatus.OK, result.getStatusCode());
  }

  @Test
  public void testInformationProduct() {
    //arrange
    Optional<InformationProduct> informationProduct = Optional.of(new InformationProduct("name"));
    when(service.getInformationProduct("namespace/name")).thenReturn(informationProduct);

    InformationProductController controller = new InformationProductController(service);
    init(controller);

    //act
    ResponseEntity<InformationProduct> result = controller
        .getInformationProduct("name", "namespace/");

    //assert
    assertEquals("Models should be the same", informationProduct.get(), result.getBody());
    assertEquals("Status code should be 200", HttpStatus.OK, result.getStatusCode());
  }

  @Test
  public void testInformationProductNotFound() {
    //arrange
    Optional<InformationProduct> informationProduct = Optional.empty();
    when(service.getInformationProduct("default/name")).thenReturn(informationProduct);

    InformationProductController controller = new InformationProductController(service);
    init(controller);

    //act
    ResponseEntity<InformationProduct> result = controller.getInformationProduct("name", null);

    //assert
    assertEquals("Status code should be 404", HttpStatus.NOT_FOUND, result.getStatusCode());
  }

  @Test
  public void testInformationProductQuery() {
    //arrange
    Model model = new LinkedHashModel();
    when(service.queryInformationProduct(eq("default/name"), any())).thenReturn(model);

    InformationProductController controller = new InformationProductController(service);
    init(controller);

    //act
    ResponseEntity<Model> result = controller.queryInformationProduct("name", null, request);

    //assert
    assertEquals("Models should be the same", model, result.getBody());
    assertEquals("Status code should be 200", HttpStatus.OK, result.getStatusCode());
  }

  @Test
  public void testFetchesParametersFromRequest() {
    //arrange
    Model model = new LinkedHashModel();
    when(service.queryInformationProduct(any(), any())).thenReturn(model);
    Map<String, String[]> map = new HashMap<>();
    map.put("parameter", new String[]{"value"});
    when(request.getParameterMap()).thenReturn(map);

    InformationProductController controller = new InformationProductController(service);
    init(controller);

    //act
    controller.queryInformationProduct("name", null, request);

    //assert

    verify(service).queryInformationProduct(eq("default/name"), argumentCaptor.capture());
    Map<String, String> value = argumentCaptor.getValue();
    assertEquals("Size should be 1", 1, value.size());
    assertEquals("Parameter should be present", "parameter",
        value.keySet().stream().findFirst().get());
    assertEquals("Value should been filled", "value", value.values().stream().findFirst().get());
  }

  @Test
  public void testFetchesOnlyTheFirstValue() {
    //arrange
    Model model = new LinkedHashModel();
    when(service.queryInformationProduct(any(), any())).thenReturn(model);
    Map<String, String[]> map = new HashMap<>();
    map.put("parameter", new String[]{"value1", "value2"});
    when(request.getParameterMap()).thenReturn(map);

    InformationProductController controller = new InformationProductController(service);
    init(controller);

    //act
    controller.queryInformationProduct("name", null, request);

    //assert

    verify(service).queryInformationProduct(eq("default/name"), argumentCaptor.capture());
    Map<String, String> value = argumentCaptor.getValue();
    assertEquals("Size should be 1", 1, value.size());
    assertEquals("Parameter should be present", "parameter",
        value.keySet().stream().findFirst().get());
    assertEquals("Value1 should been filled, value2 ignored", "value1",
        value.values().stream().findFirst().get());
  }
}
