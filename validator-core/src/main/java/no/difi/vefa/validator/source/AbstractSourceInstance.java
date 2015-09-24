package no.difi.vefa.validator.source;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import no.difi.asic.AsicReader;
import no.difi.asic.AsicReaderFactory;
import no.difi.asic.SignatureMethod;
import no.difi.vefa.validator.api.Properties;
import no.difi.vefa.validator.api.SourceInstance;
import no.difi.xsd.asic.model._1.Certificate;
import no.difi.xsd.vefa.validator._1.Artifacts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

class AbstractSourceInstance implements SourceInstance {

    private static Logger logger = LoggerFactory.getLogger(AbstractSourceInstance.class);

    protected static AsicReaderFactory asicReaderFactory = AsicReaderFactory.newFactory(SignatureMethod.CAdES);

    protected static JAXBContext jaxbContext;

    static {
        try {
            jaxbContext = JAXBContext.newInstance(Artifacts.class);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    protected FileSystem fileSystem;

    public AbstractSourceInstance(Properties properties) {
        if (properties.getBoolean("source.in-memory"))
            fileSystem = Jimfs.newFileSystem(Configuration.unix());
        else {
            throw new IllegalStateException("Source storage not defined.");
        }
    }

    protected void unpackContainer(AsicReader asicReader, String targetName) throws IOException {
        // Prepare copying from asice-file to in-memory filesystem
        Path targetDirectory = fileSystem.getPath(targetName);

        // Copy content
        String filename;
        while ((filename = asicReader.getNextFile()) != null) {
            Path outputPath = targetDirectory.resolve(filename);
            Files.createDirectories(outputPath.getParent());
            logger.debug("{}", outputPath);

            asicReader.writeFile(outputPath);
        }

        // Close asice-file
        asicReader.close();

        // Listing signatures
        for (Certificate certificate : asicReader.getAsicManifest().getCertificates())
            logger.info(String.format("Signature: %s", certificate.getSubject()));

        // TODO Validate certificate?
    }

    @Override
    public FileSystem getFileSystem() {
        return fileSystem;
    }
}
