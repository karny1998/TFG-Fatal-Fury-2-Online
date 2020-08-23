package lib.utils;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

/**
 * The type Null resolver.
 */
// Clase usada por el lector de xml (xmlReader) que implementa Entity resolver
public class NullResolver implements EntityResolver {
    /**
     * Resolve entity input source.
     *
     * @param publicId the public id
     * @param systemId the system id
     * @return the input source
     * @throws SAXException the sax exception
     * @throws IOException  the io exception
     */
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException,
            IOException {
        return new InputSource(new StringReader(""));
    }
}