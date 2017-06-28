package org.dotwebstack.data.repository.impl;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rick Fleuren on 6/15/2017.
 */
@org.springframework.stereotype.Repository("File")
public class ConfigurationRepository extends Rdf4JRepository {

    private Repository repository;

    @Autowired
    public ConfigurationRepository(ResourceLoader resourceLoader,
                                   @Value("${default.namespace}") String defaultNamespace,
                                   @Value("${config.init.foldername}") String repositoryName,
                                   @Value("${config.init.files}") String... files)
            throws IOException {
        MemoryStore memoryStore = new MemoryStore(new File(repositoryName));

        repository = new org.eclipse.rdf4j.repository.sail.SailRepository(memoryStore);
        repository.initialize();

        clearData();
        initialiseData(resourceLoader, defaultNamespace, files);
    }

    private void clearData() {
        performQuery(c -> {
            c.clearNamespaces();
            c.clear();
        });
    }

    private void initialiseData(ResourceLoader resourceLoader, String defaultNamespace, String[] files) throws IOException {
        if (files == null) {
            return;
        }

        Map<String, RDFFormat> fileFormats = getFileFormats();

        Model result = new LinkedHashModel();

        for (String fileName : files) {
            String extension = FilenameUtils.getExtension(fileName);
            Resource resource = resourceLoader.getResource(fileName);

            Model model = Rio.parse(resource.getInputStream(), defaultNamespace, fileFormats.get(extension));
            result.addAll(model);
        }

        //add the model to the repo
        performQuery(c -> {
            c.add(result);
            c.commit();
        });
    }

    private Map<String, RDFFormat> getFileFormats() {
        Field[] fields = RDFFormat.class.getDeclaredFields();

        Map<String, RDFFormat> formatMap = new HashMap<>();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) && (RDFFormat.class == field.getType())) {
                try {
                    RDFFormat format = (RDFFormat) field.get(null);
                    for (String extension : format.getFileExtensions()) {
                        formatMap.put(extension, format);
                    }

                } catch (IllegalAccessException e) {
                    //Ignore this format
                }
            }
        }
        return formatMap;
    }

    @Override
    public Repository getRepository() {
        return repository;
    }
}
