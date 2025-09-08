package uk.gov.moj.cpp.system.documentgenerator.client;

import java.io.IOException;
import java.util.UUID;

import javax.json.JsonObject;

public interface DocumentGeneratorClient {

    /**
     * Generate PDF document from json data and template identifier
     * @param jsonData data to generate pdf template
     * @param templateIdentifier pre-registered template identifier
     * @param userId user requesting document generation
     * @return byte array representation of generated document
     * @throws IOException during converting generated document to byte array
     * @throws DocumentGeneratorClientException if document generation wasn't successful
     */
    byte[] generatePdfDocument(final JsonObject jsonData, final String templateIdentifier, final UUID userId) throws IOException;

    /**
     * Generate HTML document from json data and thymeleaf template identifier
     * @param jsonData data to generate thymeleaf template
     * @param templateIdentifier pre-registered template identifier
     * @return byte array representation of generated document
     * @throws IOException during converting generated document to byte array
     * @throws DocumentGeneratorClientException if document generation wasn't successful
     */
    byte[] generateThymeleafDocument(final JsonObject jsonData, final String templateIdentifier) throws IOException;

    /**
     * Generate Word document from json data and template identifier
     * @param jsonData data to generate pdf template
     * @param templateIdentifier pre-registered template identifier
     * @param userId user requesting document generation
     * @return byte array representation of generated document
     * @throws IOException during converting generated document to byte array
     * @throws DocumentGeneratorClientException if document generation wasn't successful
     */
    byte[] generateWordDocument(final JsonObject jsonData, final String templateIdentifier, final UUID userId) throws IOException;
}
