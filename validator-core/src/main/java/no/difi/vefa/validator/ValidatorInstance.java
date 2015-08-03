package no.difi.vefa.validator;

import no.difi.vefa.validator.api.Checker;
import no.difi.vefa.validator.api.Presenter;
import no.difi.vefa.validator.api.SourceInstance;
import no.difi.xsd.vefa.validator._1.FileType;
import no.difi.xsd.vefa.validator._1.StylesheetType;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains CheckerPools and Configuration, and is entry point for validation.
 * One validator is loaded when initiating ValidatorInstance.
 */
class ValidatorInstance {

    private ValidatorEngine validatorEngine;

    private Map<String, Configuration> configurationMap = new HashMap<>();

    private GenericKeyedObjectPool<String, Checker> checkerPool;
    private GenericKeyedObjectPool<String, Presenter> presenterPool;

    /**
     * Constructor loading artifacts and pools for validations.
     *
     * @param sourceInstance Source for validation artifacts
     * @throws ValidatorException
     */
    ValidatorInstance(SourceInstance sourceInstance) throws ValidatorException {
        // Create a new engine
        validatorEngine = new ValidatorEngine(sourceInstance);

        // New pool for checkers
        checkerPool = new GenericKeyedObjectPool<>(new CheckerPoolFactory(validatorEngine));
        checkerPool.setBlockWhenExhausted(false);

        // New pool for presenters
        presenterPool = new GenericKeyedObjectPool<>(new PresenterPoolFactory(validatorEngine));
        presenterPool.setBlockWhenExhausted(false);
    }

    /**
     * List of packages supported by validator.
     *
     * @return List of packages.
     */
    public List<String> getPackages() {
        return validatorEngine.getPackages();
    }

    /**
     * Return validation configuration.
     *
     * @param documentDeclaration Fetch configuration using declaration.
     */
    public Configuration getConfiguration(DocumentDeclaration documentDeclaration) throws ValidatorException {
        // Check cache of configurations ready to use.
        if (configurationMap.containsKey(documentDeclaration.toString()))
            return configurationMap.get(documentDeclaration.toString());

        Configuration configuration = new Configuration(validatorEngine.getConfiguration(documentDeclaration));
        configuration.normalize(validatorEngine);
        configurationMap.put(documentDeclaration.toString(), configuration);

        return configuration;
    }

    /**
     * Present document using stylesheet
     *
     * @param stylesheet Stylesheet identifier from configuration.
     * @param document Document used for styling.
     * @param outputStream Stream for dumping of result.
     */
    public void present(StylesheetType stylesheet, Document document, OutputStream outputStream) throws Exception {
        Presenter presenter = presenterPool.borrowObject(stylesheet.getPath());
        presenter.present(document, outputStream);
        presenterPool.returnObject(stylesheet.getPath(), presenter);
    }

    /**
     * Validate document using a file definition.
     *
     * @param fileType File definition from configuration.
     * @param document Document to validate.
     * @param configuration Complete configuration
     * @return Result of validation.
     */
    public Section check(FileType fileType, Document document, Configuration configuration) throws Exception {
        Checker checker = checkerPool.borrowObject(fileType.getPath());
        Section section = checker.check(document, configuration);
        checkerPool.returnObject(fileType.getPath(), checker);
        return section;
    }
}
