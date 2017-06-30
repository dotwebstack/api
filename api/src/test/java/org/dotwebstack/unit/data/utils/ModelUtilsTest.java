package org.dotwebstack.unit.data.utils;

import static java.util.stream.Collectors.toList;
import static org.dotwebstack.utils.TestUtils.createArtist;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Stream;
import org.dotwebstack.data.utils.ModelUtils;
import org.dotwebstack.data.utils.helper.Subject;
import org.dotwebstack.test.categories.Categories;
import org.dotwebstack.utils.TestUtils;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by Rick Fleuren on 6/30/2017.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public class ModelUtilsTest {

  @Test
  public void extractHelpers() {
    //arrange
    Model model = TestUtils.createArtist("pablo").build();

    //act
    List<Subject> subjects = ModelUtils.extractHelpers(model);

    //assert
    assertEquals("Should be 1 subject", 1, subjects.size());

    Subject pablo = subjects.get(0);
    assertEquals("Should be picasso subject", "http://example.org/pablo", pablo.getSubject());

    assertEquals("Should be picasso name with full namespace", "pablo",
        pablo.getValue("http://xmlns.com/foaf/0.1/lastName").get());
    assertEquals("Should be artist type with full namespace", "http://example.org/Artist",
        pablo.getValue("http://www.w3.org/1999/02/22-rdf-syntax-ns#type").get());

  }

  @Test
  public void extractHelpersWithExpandingNamespaces() {
    //arrange
    Model model = TestUtils.createArtist("pablo").build();

    //act
    List<Subject> subjects = ModelUtils.extractHelpers(model);

    //assert
    assertEquals("Should be 1 subject", 1, subjects.size());

    Subject pablo = subjects.get(0);
    assertTrue("Should contain picasso name", pablo.containsValue("foaf:lastName"));
    assertTrue("Should contain artist", pablo.containsValue("rdf:type"));
  }

  @Test
  public void splitBySubjects() {
    //arrange
    Model picasso = createArtist("Picasso").build();
    Model ross = createArtist("Ross").build();

    Model combined = new LinkedHashModel(
        Stream.concat(picasso.stream(), ross.stream()).collect(toList()));

    //act
    List<Model> models = ModelUtils.filterBySubject(combined);

    //assert
    assertEquals("Size should be 2", 2, models.size());
  }


}
