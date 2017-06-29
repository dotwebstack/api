package org.dotwebstack.unit.data.client;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.util.function.Consumer;
import java.util.function.Function;
import org.dotwebstack.data.client.TripleStoreClient;
import org.dotwebstack.data.client.impl.SailMemoryTripleStoreClient;
import org.dotwebstack.data.repository.TripleStoreRepository;
import org.dotwebstack.test.categories.Categories;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

/**
 * Created by Rick Fleuren on 6/15/2017.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public abstract class TripleStoreClientTest {

  @InjectMocks
  protected TripleStoreClient client = new SailMemoryTripleStoreClient();
  @Mock
  TripleStoreRepository repository;
  @Mock
  RepositoryConnection connection;
  @Mock
  ValueFactory valueFactory;
  @Captor
  private ArgumentCaptor<Consumer<RepositoryConnection>> consumerCaptor;

  @Captor
  private ArgumentCaptor<Function<RepositoryConnection, ?>> functionCaptor;

  protected void initConnectionConsumer() {
    doAnswer(invocation -> {
      ((Consumer<RepositoryConnection>) invocation.getArguments()[0]).accept(connection);
      return null;
    }).when(repository).performQuery((Consumer<RepositoryConnection>) any());
  }

  protected void initConnectionFunction() {
    when(repository.performQuery((Function<RepositoryConnection, ?>) any())).thenAnswer(
        new Answer<Object>() {
          public Object answer(InvocationOnMock invocation) {
            return ((Function<RepositoryConnection, ? extends Object>) invocation.getArguments()[0])
                .apply(connection);
          }
        });
  }
}
