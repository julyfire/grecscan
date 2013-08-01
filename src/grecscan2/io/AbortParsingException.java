/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.io;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;

/**
 * Tells the {@link SAXReader} to stop the parsing. Exceptions are then wrapped in a {@link org.dom4j.DocumentException}, so you need to check the cause with <br />
 * <code>if (e.getNestedException() != null && e.getNestedException() instanceof AbortParsingException) {...}</code>
 * @author sberthouzoz
 *
 */
public class AbortParsingException extends RuntimeException {
    private Document document;

    public AbortParsingException(String message) {
        super(message);
    }

    public AbortParsingException(Document parsedDocument, String message) {
        this(message);
        this.document = parsedDocument;
    }

    public Document getDocument() {
        return document;
    }

}