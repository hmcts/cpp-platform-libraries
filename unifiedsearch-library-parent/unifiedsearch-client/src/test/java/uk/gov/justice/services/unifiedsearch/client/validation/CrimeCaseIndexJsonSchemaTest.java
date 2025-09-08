package uk.gov.justice.services.unifiedsearch.client.validation;


import static org.junit.jupiter.api.Assertions.fail;
import static uk.gov.justice.services.test.utils.core.reflection.ReflectionUtil.setField;
import static uk.gov.justice.services.unifiedsearch.client.validation.JsonUtils.jsonObjectFromFile;

import javax.json.JsonObject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CrimeCaseIndexJsonSchemaTest {

    private static final String JSON_SCHEMA_FILE_PATH = "/json/schema/crime-case-index-schema.json";

    private JsonDocumentValidator validator = new JsonDocumentValidator();

    @BeforeEach
    public void setUp() {
        setField(validator, "validationExceptionHandler", new ValidationExceptionHandler());
    }


    @Test
    public void shouldValidateDocumentWithOneCase() throws Exception {
        final JsonObject document = jsonObjectFromFile("json/valid-crime-case-index-document.json");

        try {
            validator.validate(document, JSON_SCHEMA_FILE_PATH);
        }
        catch (final TransformationException transEx) {
            System.err.println(transEx.getValidationFailures());
            fail();
        }
    }

    @Test
    public void shouldValidateDocumentWithOneCaseAndLAAData() throws Exception {
        final JsonObject document = jsonObjectFromFile("json/valid-crime-case-index-document-with-laa-fields.json");

        try {
            validator.validate(document, JSON_SCHEMA_FILE_PATH);
        }
        catch (final TransformationException transEx) {
            System.err.println(transEx.getValidationFailures());
            fail();
        }
    }

    @Test
    public void shouldValidateDocumentWithCaseDocumentsArray() throws Exception {
        final JsonObject document = jsonObjectFromFile("json/valid-array-of-crime-case-index-documents.json");

        try {
            validator.validate(document, JSON_SCHEMA_FILE_PATH);
        }
        catch (final TransformationException transEx) {
            System.err.println(transEx.getValidationFailures());
            fail();
        }

    }


    @Test
    public void shouldValidateDocumentWithCaseDocumentsArraycContainingOneCase() throws Exception {
        final JsonObject document = jsonObjectFromFile("json/case-document-single-element.json");

        try {
            validator.validate(document, JSON_SCHEMA_FILE_PATH);
        }
        catch (final TransformationException transEx) {
            System.err.println(transEx.getValidationFailures());
            fail();
        }

    }
}
