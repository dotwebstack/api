package org.dotwebstack.api.controller;

import org.dotwebstack.data.client.TripleStoreClient;
import org.eclipse.rdf4j.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */

@RestController
@RequestMapping("/api/v1/models")
public class ModelController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelController.class);

    @Value("${default.namespace}")
    String defaultNamespace;

    private final TripleStoreClient client;

    @Autowired
    public ModelController(TripleStoreClient client) {
        this.client = client;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Model> getModels() {
        return ResponseEntity.ok(client.query());
    }

    @RequestMapping(path = "/{subject}")
    public ResponseEntity<Model> getModel(@PathVariable("subject") String subject, @RequestParam(value = "namespace", required = false) String namespace) {
        String result = getNamespace(namespace);
        return ResponseEntity.ok(client.queryBySubject(result + subject));
    }

    @RequestMapping(path = "/{subject}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteModel(@PathVariable("subject") String subject, @RequestParam(value = "namespace", required = false) String namespace) {
        String result = getNamespace(namespace);
        client.deleteBySubject(result + subject);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<Model> addModel(@RequestBody Model model) {
        Model result = client.add(model);
        return ResponseEntity.ok(result);
    }

    @RequestMapping(method = RequestMethod.PATCH)
    public ResponseEntity<Model> updateModel(@RequestBody Model model) {
        Model result = client.update(model);
        return ResponseEntity.ok(result);
    }

    private String getNamespace(String namespace) {
        return namespace == null ? defaultNamespace : namespace;
    }
}
