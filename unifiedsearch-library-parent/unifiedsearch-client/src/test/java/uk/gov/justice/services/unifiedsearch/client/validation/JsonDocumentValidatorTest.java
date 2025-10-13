package uk.gov.justice.services.unifiedsearch.client.validation;

import org.everit.json.schema.NullSchema;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.json.JsonObject;
import java.io.IOException;

import static org.codehaus.groovy.runtime.InvokerHelper.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.messaging.JsonObjects.jsonBuilderFactory;
import static uk.gov.justice.services.unifiedsearch.client.validation.JsonUtils.jsonObjectFromFile;

@ExtendWith(MockitoExtension.class)
public class JsonDocumentValidatorTest {

    @Mock
    private ValidationExceptionHandler validationExceptionHandler;

    @Mock
    private Schema schema;

    @InjectMocks
    private JsonDocumentValidator validator;


    @Test
    public void shouldPassValidationWhenSchemaDoesntThrowValidationException() throws IOException {
        validator.validate(jsonObjectFromFile("json/valid-crime-case-index-document.json"), "/json/schema/crime-case-index-schema.json");
        verify(schema).validate(any());
    }


    @Test
    public void shouldFailValidation() throws Exception {
        doThrow(validationException()).when(schema).validate(any());
        when(validationExceptionHandler.handleValidationException(any())).thenReturn(asList("Validation Error"));

        try {
            validator.validate(jsonObjectFromFile("json/invalid-crime-case-index-document.json"), "/json/schema/crime-case-index-schema.json");
            fail();
        }
        catch (final TransformationException tex) {
            assertEquals(tex.getValidationFailures().size(), 1);
        }


    }

    @Test
    public void shouldFailValidationForNullJsonObject() {
        assertThrows(TransformationException.class, () -> validator.validate(null, "/json/schema/crime-case-index-schema.json"));
    }

    @Test
    public void shouldFailValidationFoEmptyCaseDocumentsArray() {
        final JsonObject invalidJson = jsonBuilderFactory.createObjectBuilder().add("caseDocuments", jsonBuilderFactory.createArrayBuilder()).build();
        assertThrows(TransformationException.class, () -> validator.validate(invalidJson, "/json/schema/crime-case-index-schema.json"));
    }


    private static ValidationException validationException() {
        final String message = "required key [organisationName] not found";
        final String keyword = "required";

        final NullSchema.Builder builder = new NullSchema.Builder();
        final Schema violatedSchema = new NullSchema(builder);
        return  new ValidationException(violatedSchema, message, keyword);
    }



}