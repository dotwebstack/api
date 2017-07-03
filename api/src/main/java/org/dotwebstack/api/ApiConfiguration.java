package org.dotwebstack.api;

import java.util.HashMap;
import java.util.List;
import org.dotwebstack.api.converter.RdfCsvConverter;
import org.dotwebstack.api.converter.RdfHtmlConverter;
import org.dotwebstack.api.converter.RdfPdfConverter;
import org.dotwebstack.api.converter.RdfRioMessageConverter;
import org.dotwebstack.api.converter.RdfTextConverter;
import org.dotwebstack.api.converter.ResourceConverter;
import org.dotwebstack.api.converter.graphml.RdfGraphmlConverter;
import org.dotwebstack.api.converter.graphml.RdfYedConverter;
import org.dotwebstack.api.converter.office.RdfExcelConverter;
import org.dotwebstack.api.converter.office.RdfExcelOpenXmlConverter;
import org.dotwebstack.api.converter.office.RdfWordConverter;
import org.dotwebstack.api.converter.office.RdfWordOpenXmlConverter;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */

@Configuration
@EnableWebMvc
@PropertySource({
    "settings.properties",
    "repository.sail.properties",
    "repository.configuratie.properties",
    "repository.sparql.properties",
    "repository.virtuoso.properties"})
public class ApiConfiguration extends WebMvcConfigurerAdapter {

  private static HashMap<String, HttpMessageConverter<?>> converters;

  static {
    converters = new HashMap<>();
    //TODO: default xml rules, sparql, json rules, txt,  pdf, xmi, graphml, yed
    //see: https://github.com/architolk/Linked-Data-Theatre/blob/master/docs/Content-negotiation.md
    converters.put("html", new RdfHtmlConverter());
    converters.put("json", new RdfRioMessageConverter(RDFFormat.JSONLD));
    converters.put("jsonld", new RdfRioMessageConverter(RDFFormat.JSONLD));
    converters.put("xml", new RdfRioMessageConverter(RDFFormat.RDFXML));
    converters.put("rdf", new RdfRioMessageConverter(RDFFormat.RDFXML));
    converters.put("ttl", new RdfRioMessageConverter(RDFFormat.TURTLE));
    converters.put("rdf-json", new RdfRioMessageConverter(RDFFormat.RDFJSON));
    converters.put("n-triple", new RdfRioMessageConverter(RDFFormat.NTRIPLES));

    //write only converters
    converters.put("csv", new RdfCsvConverter());
    converters.put("pdf", new RdfPdfConverter());
    converters.put("graphml", new RdfGraphmlConverter());
    converters.put("txt", new RdfTextConverter());
    converters.put("yed", new RdfYedConverter());

    //office
    converters.put("xls", new RdfExcelConverter());
    converters.put("xlsx", new RdfExcelOpenXmlConverter());
    converters.put("doc", new RdfWordConverter());
    converters.put("docx", new RdfWordOpenXmlConverter());
  }

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    configurer
        .defaultContentType(MediaType.valueOf(RDFFormat.JSONLD.getDefaultMIMEType()))
        .parameterName("format")
        .favorPathExtension(true)
        .favorParameter(true)
        .ignoreUnknownPathExtensions(false)
        .ignoreAcceptHeader(false)
        .useJaf(true);

    for (String extension : converters.keySet()) {
      converters.get(extension).getSupportedMediaTypes()
          .forEach(m -> configurer.mediaType(extension, m));
    }

  }

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(new StringHttpMessageConverter());
    converters.addAll(ApiConfiguration.converters.values());

    converters.add(new MappingJackson2HttpMessageConverter());
  }

  @Autowired
  public void addResourceLoader(ResourceLoader loader) {
    for (HttpMessageConverter converter : converters.values()) {
      if (converter instanceof ResourceConverter) {
        ((ResourceConverter) converter).setResourceLoader(loader);
      }
    }
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {

    registry.addResourceHandler("swagger-ui.html")
        .addResourceLocations("classpath:/META-INF/resources/");

    registry.addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");

  }
}
