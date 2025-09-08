package uk.gov.justice.services.unifiedsearch.client.validation;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonObject;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonDocumentValidator {

    @Inject
    private ValidationExceptionHandler validationExceptionHandler;

    private Schema schema;

    private void loadSchema(final String schemaFilePath) throws IOException {
        try (final InputStream inputStream = getClass().getResourceAsStream(schemaFilePath)) {
            final JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
            schema = SchemaLoader.load(rawSchema);
        }
    }


    public void validate(final JsonObject crimeCaseDocument, final String schemaFilePath) {

        if (crimeCaseDocument != null) {
            try {
                if (schema == null) {
                    loadSchema(schemaFilePath);
                }
                schema.validate(new JSONObject(crimeCaseDocument.toString()));

                final JsonArray documents = crimeCaseDocument.getJsonArray("caseDocuments");
                if (documents != null && documents.size() == 0) {
                    throw new TransformationException("Provided crime case document contains an empty caseDocument array!");
                }

            } catch (final ValidationException validationException) {
                throw new TransformationException("Invalid JSON", validationExceptionHandler.handleValidationException(validationException), validationException);
            } catch (final IOException ioex) {
                throw new TransformationException("Unexpected IOException loading JSON schema file for crime case document!", ioex);
            }
        } else {
            throw new TransformationException("Provided crime case document is null!");
        }

    }
}
