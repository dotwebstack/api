package org.dotwebstack.data.service.impl;

import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import org.dotwebstack.data.client.TripleStoreClient;
import org.dotwebstack.data.client.impl.TripleStoreClientImpl;
import org.dotwebstack.data.repository.impl.ConfigurationRepository;
import org.dotwebstack.data.service.InformationProduct;
import org.dotwebstack.data.service.InformationProductService;
import org.dotwebstack.data.service.QueryParser;
import org.dotwebstack.data.utils.QueryUtils;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Rick Fleuren on 6/15/2017.
 */
@Service
public class InformationProductServiceImpl implements InformationProductService {

  public static final String ELMO_INFORMATION_PRODUCT = "elmo:InformationProduct";
  public static final String ELMO_QUERY = "elmo:query";
  public static final String ELMO_ADAPTER = "elmo:adapter";
  public static final String SELECT_ALL_QUERY = "SELECT ?s ?p ?o WHERE { ?s rdf:type %1$s. ?s ?p ?o }";
  public static final String SELECT_ONE_QUERY = "SELECT ?p ?o WHERE { <%1$s> rdf:type %2$s. <%1$s> ?p ?o }";
  private final TripleStoreClientImpl<ConfigurationRepository> configurationClient;
  private List<TripleStoreClient> clients;

  public InformationProductServiceImpl(
      TripleStoreClientImpl<ConfigurationRepository> configurationClient) {
    this.configurationClient = configurationClient;
  }

  @Autowired
  public InformationProductServiceImpl(
      TripleStoreClientImpl<ConfigurationRepository> configurationClient,
      List<TripleStoreClient> clients) {
    this.configurationClient = configurationClient;
    this.clients = clients;
  }

  /**
   * Get all the information products
   *
   * @return information products names
   */
  @Override
  public List<InformationProduct> getInformationProducts() {
    String query = String.format(SELECT_ALL_QUERY, ELMO_INFORMATION_PRODUCT);
    List<Map<String, Value>> result = configurationClient.select(query);

    return convertToInformationProducts(result);
  }

  /**
   * Get specific information products
   *
   * @return information products names
   */
  @Override
  public Optional<InformationProduct> getInformationProduct(String name) {
    String subject = QueryUtils.expand(name);
    String query = String.format(SELECT_ONE_QUERY, subject, ELMO_INFORMATION_PRODUCT);
    List<Map<String, Value>> result = configurationClient.select(query);

    //as we didn't query on the subject, because its already known, add it to the result set, so that we can use the same parse method
    result.forEach(m -> m.put("s", SimpleValueFactory.getInstance().createLiteral(subject)));

    List<InformationProduct> informationProducts = convertToInformationProducts(result);

    //Result should never exceed 1
    assert informationProducts.size() <= 1;

    return informationProducts.stream().findFirst();
  }

  /**
   * Get specific information products
   *
   * @return information products names
   */
  @Override
  public Model queryInformationProduct(String name, Map<String, String> parameters) {
    InformationProduct product = getInformationProduct(name)
        .orElseThrow(() -> new IllegalStateException("InformationProduct not found"));
    String query = buildQuery(product.getQuery(), parameters);

    //is every parameter filled, does it still contain
    if (query.contains("@")) {
      throw new IllegalStateException("Not every parameter is filled: " + query);
    }

    //default datasource
    if (product.getAdapter() == null) {
      return configurationClient.construct(query);
    } else {
      if (clients == null) {
        return configurationClient.construct(query);
      }

      TripleStoreClient resultingClient = clients.stream()
          .filter(s -> product.getAdapter().equals(s.getAdapterName()))
          .findFirst().orElse(configurationClient);

      return resultingClient.construct(query);
    }
  }

  private String buildQuery(String template, Map<String, String> parameters) {
    if (parameters == null) {
      return template;
    }

    String query = template;
    for (String key : parameters.keySet()) {
      query = query.replace("@" + key + "@", parameters.get(key));
    }
    return query;
  }

  private List<InformationProduct> convertToInformationProducts(
      List<Map<String, Value>> queryResult) {
    Map<String, InformationProduct> result = new HashMap<>();

    filterBy(queryResult, "o", ELMO_INFORMATION_PRODUCT, i -> {
      String name = i.get("s").stringValue();
      result.put(name, new InformationProduct(name));
    });

    filterBy(queryResult, "p", ELMO_QUERY, i -> {
      String name = i.get("s").stringValue();
      String query = i.get("o").stringValue();

      //Name should be present, because of the query, if its not, the query has been tampered with
      assert result.containsKey(name);

      InformationProduct informationProduct = result.get(name);
      informationProduct.setQuery(query);

      List<String> parameters = QueryParser.getParametersFromQuery(query);
      informationProduct.setParameters(parameters);
    });

    filterBy(queryResult, "p", ELMO_ADAPTER, i -> {
      String name = i.get("s").stringValue();
      String adapter = i.get("o").stringValue();
      result.get(name).setAdapter(adapter);
    });

    return result.values().stream().collect(toList());
  }

  private void filterBy(List<Map<String, Value>> queryResult, String parameter, String shouldEquals,
      Consumer<Map<String, Value>> doAction) {
    queryResult.stream()
        .filter(q -> q.get(parameter).stringValue().equals(QueryUtils.expand(shouldEquals)))
        .forEach(doAction);
  }
}
