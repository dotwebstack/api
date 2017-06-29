package org.dotwebstack.integration.data.service;

import static junit.framework.TestCase.assertFalse;
import static org.dotwebstack.data.service.impl.InformationProductServiceImpl.ELMO_ADAPTER;
import static org.dotwebstack.data.service.impl.InformationProductServiceImpl.ELMO_INFORMATION_PRODUCT;
import static org.dotwebstack.data.service.impl.InformationProductServiceImpl.ELMO_QUERY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;
import org.dotwebstack.data.client.impl.TripleStoreClientImpl;
import org.dotwebstack.data.repository.impl.ConfigurationRepository;
import org.dotwebstack.data.service.InformationProduct;
import org.dotwebstack.data.service.InformationProductService;
import org.dotwebstack.data.service.impl.InformationProductServiceImpl;
import org.dotwebstack.data.utils.QueryUtils;
import org.dotwebstack.integration.data.service.configuration.ServiceConfiguration;
import org.dotwebstack.test.categories.Categories;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Rick Fleuren on 6/16/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Category(Categories.IntegrationTests.class)
@ContextConfiguration(classes = ServiceConfiguration.class)
public class InformationProductServiceTest {

  @Autowired
  protected TripleStoreClientImpl<ConfigurationRepository> tripleStore;

  @Before
  public void before() {
    tripleStore.clearAllTriples();
  }

  @Test
  public void testQuery() {
    //arrange
    InformationProductService service = new InformationProductServiceImpl(tripleStore);
    Model informationProduct = createInformationProduct("name", ELMO_INFORMATION_PRODUCT, "query")
        .build();
    tripleStore.add(informationProduct);

    //act
    List<InformationProduct> products = service.getInformationProducts();

    //assert
    assertEquals("Size should be 1", 1, products.size());

    InformationProduct product = products.get(0);
    assertEquals("Name should be name", "http://example.org/name", product.getName());
    assertEquals("Query should be query", "query", product.getQuery());
    assertEquals("No parameters", 0, product.getParameters().size());
  }

  @Test
  public void testEmptyQueryObject() {
    //arrange
    InformationProductService service = new InformationProductServiceImpl(tripleStore);
    Model informationProduct = createInformationProduct("name", ELMO_INFORMATION_PRODUCT, null)
        .build();
    tripleStore.add(informationProduct);

    //act
    List<InformationProduct> products = service.getInformationProducts();

    //assert
    assertEquals("Size should be 1", 1, products.size());

    InformationProduct product = products.get(0);
    assertEquals("Name should be name", "http://example.org/name", product.getName());
    assertNull("Query should be null", product.getQuery());
  }

  @Test
  public void testIncorrectData() {
    //arrange
    InformationProductService service = new InformationProductServiceImpl(tripleStore);
    Model informationProduct = createInformationProduct("name", "elmo:Othertype", null).build();
    tripleStore.add(informationProduct);

    //act
    List<InformationProduct> products = service.getInformationProducts();

    //assert
    assertEquals("Size should be 0, because its not an information product", 0, products.size());
  }

  @Test
  public void testSingleQuery() {
    //arrange
    InformationProductService service = new InformationProductServiceImpl(tripleStore);
    tripleStore.add(createInformationProduct("name1", ELMO_INFORMATION_PRODUCT, "query").build());
    tripleStore.add(createInformationProduct("name2", ELMO_INFORMATION_PRODUCT, "query").build());

    //act
    Optional<InformationProduct> product = service
        .getInformationProduct("http://example.org/name1");

    //assert
    assertTrue("Product should be present", product.isPresent());
    assertEquals("Name should be name", "http://example.org/name1", product.get().getName());
    assertEquals("Query should be query", "query", product.get().getQuery());
    assertEquals("Adapter should be adapter", "adapter", product.get().getAdapter());
    assertEquals("No parameters", 0, product.get().getParameters().size());
  }


  @Test
  public void testNonFound() {
    //arrange
    InformationProductService service = new InformationProductServiceImpl(tripleStore);
    tripleStore.add(createInformationProduct("name1", ELMO_INFORMATION_PRODUCT, "query").build());
    tripleStore.add(createInformationProduct("name2", ELMO_INFORMATION_PRODUCT, "query").build());

    //act
    Optional<InformationProduct> product = service.getInformationProduct("elmo:name_not_found");

    //assert
    assertFalse("Product should be present", product.isPresent());
  }

  private ModelBuilder createInformationProduct(String name, String type, String query) {

    ModelBuilder builder = new ModelBuilder();

    builder
        .setNamespace("ex", "http://example.org/")
        .subject("ex:" + name)
        .add(RDF.TYPE, SimpleValueFactory.getInstance().createIRI(QueryUtils.expand(type)))
        .add(QueryUtils.expand(ELMO_ADAPTER), "adapter");

    if (query != null) {
      builder.add(QueryUtils.expand(ELMO_QUERY), query);
    }

    return builder;
  }
}
