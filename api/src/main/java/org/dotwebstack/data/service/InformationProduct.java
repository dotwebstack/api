package org.dotwebstack.data.service;

import java.util.List;

/**
 * Created by Rick Fleuren on 6/16/2017.
 */
public class InformationProduct {

  private final String name;
  private String query;
  private List<String> parameters;
  private String adapter;

  public InformationProduct(String name) {
    this.name = name;
    this.setQuery(getQuery());
    this.setParameters(getParameters());
  }

  public String getName() {
    return name;
  }

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public String getAdapter() {
    return adapter;
  }

  public void setAdapter(String adapter) {
    this.adapter = adapter;
  }

  public List<String> getParameters() {
    return parameters;
  }

  public void setParameters(List<String> parameters) {
    this.parameters = parameters;
  }
}
