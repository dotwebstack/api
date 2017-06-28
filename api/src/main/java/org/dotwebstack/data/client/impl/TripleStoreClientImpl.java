package org.dotwebstack.data.client.impl;

import org.apache.commons.lang3.ClassUtils;
import org.dotwebstack.data.client.TripleStoreClient;
import org.dotwebstack.data.repository.TripleStoreRepository;
import org.dotwebstack.data.utils.ModelUtils;
import org.dotwebstack.data.utils.QueryUtils;
import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.query.*;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */
public abstract class TripleStoreClientImpl<R extends TripleStoreRepository> implements TripleStoreClient {

    private static final Logger LOG = LoggerFactory.getLogger(TripleStoreClientImpl.class);

    private String adapterName;

    @Autowired
    private R repository;

    protected TripleStoreClientImpl(String adapterName) {
        this.adapterName = adapterName;
    }

    public String getAdapterName() {
        return adapterName;
    }

    public Model add(Model model) {
        return repository.performQuery(c -> {
            LinkedHashModel result = new LinkedHashModel();
            c.add(model);

            //Get all the statements which belong to the subject:
            getModelBySubject(model, result, c);
            return result;
        });
    }

    public Model update(Model model) {
        return repository.performQuery(c -> {
            LinkedHashModel result = new LinkedHashModel();
            //Check if the statement exist with subject / predicate
            model.forEach(s -> {
                        if (c.hasStatement(s.getSubject(), s.getPredicate(), null, true)) {
                            c.remove(s.getSubject(), s.getPredicate(), null);
                        }
                    }
            );

            //add the new model to the triple store
            c.add(model);

            //Get all the statements which belong to the subject:
            getModelBySubject(model, result, c);
            return result;
        });
    }

    @Override
    public Model query() {
        return getAllStatementsAsModel();
    }

    @Override
    public Model queryBySubject(String subject) {
        assert subject != null;
        return getStatementsAsModel(subject, null, null);
    }

    @Override
    public <T> Model queryBy(String subject, String predicate, T object) {
        return getStatementsAsModel(subject, predicate, object);
    }

    @Override
    public boolean ask(String query) {
        return repository.performQuery(c -> {
            String preparedQuery = QueryUtils.addDefaultPrefixes(query);
            logQuery(preparedQuery);
            BooleanQuery booleanQuery = c.prepareBooleanQuery(preparedQuery);
            return booleanQuery.evaluate();
        });
    }

    @Override
    public List<Map<String, Value>> select(String query) {
        return repository.performQuery(c -> {
            String preparedQuery = QueryUtils.addDefaultPrefixes(query);
            logQuery(preparedQuery);
            TupleQuery tupleQuery = c.prepareTupleQuery(preparedQuery);

            List<Map<String, Value>> result = new ArrayList<>();
            try (TupleQueryResult queryResult = tupleQuery.evaluate()) {
                while (queryResult.hasNext()) {
                    Map<String, Value> values = new HashMap<>();
                    BindingSet set = queryResult.next();
                    for (String name : queryResult.getBindingNames()) {
                        values.put(name, set.getValue(name));
                    }
                    result.add(values);
                }
            }

            return result;
        });
    }

    @Override
    public Model construct(String query) {
        return repository.performQuery(c -> {
            Model model = new LinkedHashModel();
            String preparedQuery = QueryUtils.addDefaultPrefixes(query);
            logQuery(preparedQuery);
            GraphQuery graphQuery = c.prepareGraphQuery(preparedQuery);
            GraphQueryResult result = graphQuery.evaluate();

            Iterations.addAll(result, model);
            return model;
        });
    }

    @Override
    public List<Model> queryGroupedBySubject() {
        return getStatements(null, null, null, statements -> {
            List<Statement> result = new ArrayList<>();
            while (statements.hasNext()) {
                result.add(statements.next());
            }
            return ModelUtils.filterBySubject(result);
        });
    }

    @Override
    public void deleteBySubject(String subject) {
        repository.performQuery(connection -> {
            IRI subjectIri = connection.getValueFactory().createIRI(subject);
            connection.remove(subjectIri, null, null);
        });
    }

    @Override
    public void clearAllTriples() {
        repository.performQuery(connection -> {
            connection.clear();
            connection.clearNamespaces();
        });
    }

    private Model getAllStatementsAsModel() {
        return getStatementsAsModel(null, null, null);
    }

    private Model getStatementsAsModel(String subject, String predicate, Object object) {
        return getStatements(subject, predicate, object, result -> Iterations.addAll(result, new LinkedHashModel()));
    }

    private <T> T getStatements(String subject, String predicate, Object object, Function<RepositoryResult<Statement>, T> consumer) {
        return repository.performQuery(connection -> {
                    ValueFactory factory = connection.getValueFactory();
                    Resource resourceSubject = subject != null ? factory.createIRI(subject) : null;
                    IRI iriPredicate = predicate != null ? factory.createIRI(predicate) : null;
                    Value valueObject = object != null ? createValue(object, factory) : null;

                    try (RepositoryResult<Statement> result = connection.getStatements(resourceSubject, iriPredicate, valueObject)) {
                        return consumer.apply(result);
                    }
                }
        );
    }

    private Value createValue(Object object, ValueFactory factory) {
        if (object == null) {
            return null;
        }

        if (object instanceof String && ((String) object).toLowerCase().startsWith("http://")) {
            String strObj = (String) object;
            return factory.createIRI(strObj);
        } else {
            return createLiteral(object, factory);
        }
    }

    private <T> Value createLiteral(T object, ValueFactory factory) {
        try {
            Class argument = ClassUtils.isPrimitiveWrapper(object.getClass()) ? ClassUtils.wrapperToPrimitive(object.getClass()) : object.getClass();
            Method createMethod = factory.getClass().getMethod("createLiteral", argument);
            return (Value) createMethod.invoke(factory, object);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("This type was not supported: " + object.getClass(), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("This type was not supported" + object.getClass(), e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("This type was not supported" + object.getClass(), e);
        }
    }

    private void getModelBySubject(Model model, LinkedHashModel result, RepositoryConnection c) {
        model.subjects().stream()
                .map(s -> c.getStatements(s, null, null))
                .forEach(s -> Iterations.addAll(s, result));
    }

    private void logQuery(String query) {
        LOG.info("Preparing following query: \n\n" + query + "\n");
    }
}
