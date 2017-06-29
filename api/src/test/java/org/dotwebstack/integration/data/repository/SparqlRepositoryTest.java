package org.dotwebstack.integration.data.repository;

import static org.junit.Assert.assertEquals;

import org.dotwebstack.data.repository.TripleStoreRepository;
import org.dotwebstack.data.repository.impl.SPARQLRepository;
import org.dotwebstack.test.categories.Categories;
import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Created by Rick Fleuren on 6/15/2017.
 */
@Category(Categories.IntegrationTests.class)
public class SparqlRepositoryTest {

  @Test
  public void testDbpediaEndpoint() {
    //arrange
    TripleStoreRepository repository = new SPARQLRepository("http://dbpedia.org/sparql/", null);

    //act
    Model result = getModels(repository);

    //assert
    assertEquals("There should have been 1 statements added to the in SQARQL database", 1,
        result.size());
    assertEquals("It should be the Netherlands", "http://dbpedia.org/resource/Netherlands",
        result.objects().stream().map(Value::stringValue).toArray()[0]);
  }

  private Model getModels(TripleStoreRepository repository) {
    Model result = new LinkedHashModel();

    repository.performQuery(connection -> {
      ValueFactory factory = connection.getValueFactory();
      IRI amersfoort = factory.createIRI("http://dbpedia.org/resource/Amersfoort");
      IRI country = factory.createIRI("http://dbpedia.org/ontology/country");

      Iterations.addAll(connection.getStatements(amersfoort, country, null), result);
    });

    return result;
  }
}
