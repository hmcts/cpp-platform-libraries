package uk.gov.moj.cpp.accesscontrol.progression.providers;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.withJsonPath;
import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;
import static uk.gov.justice.services.test.utils.core.enveloper.EnveloperFactory.createEnveloper;
import static uk.gov.justice.services.test.utils.core.matchers.JsonEnvelopeMatcher.jsonEnvelope;
import static uk.gov.justice.services.test.utils.core.matchers.JsonEnvelopeMetadataMatcher.metadata;
import static uk.gov.justice.services.test.utils.core.matchers.JsonEnvelopePayloadMatcher.payload;
import static uk.gov.justice.services.test.utils.core.messaging.JsonEnvelopeBuilder.envelope;
import static uk.gov.justice.services.test.utils.core.messaging.MetadataBuilderFactory.metadataWithRandomUUIDAndName;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.services.core.enveloper.Enveloper;
import uk.gov.justice.services.core.requester.Requester;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.moj.cpp.accesscontrol.drools.Action;

import java.util.UUID;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class ProgressionProviderTest {

    private static final String MATERIAL_ID_KEY = "materialId";
    private static final String ATTRIBUTE_PROSECUTING_AUTHORITY = "prosecutingAuthority";
    private static final String CPS = ProsecutingAuthority.CPS.name();
    private static final String TFL = "TFL";
    private UUID materialId, caseId;

    @BeforeEach
    public void init() {
        materialId = randomUUID();
        caseId = randomUUID();
    }

    @Mock
    private Requester requester;

    @Spy
    private Enveloper enveloper = createEnveloper();

    @InjectMocks
    private ProgressionProvider progressionProvider;

    @Test
    public void shouldReturnTrueIfMaterialIsFromCPSProsecutedCase() {
        when(requester.requestAsAdmin(argThat(getCaseByMaterialIdQueryMatcher(materialId)))).thenReturn(buildProgressionResponse(CPS));

        boolean result = progressionProvider.isMaterialFromCPSProsecutedCase(buildAction(MATERIAL_ID_KEY, materialId.toString()));

        assertThat(result, is(true));
    }

    @Test
    public void shouldReturnFalseIfMaterialIsNotFromCPSProsecutedCase() {
        when(requester.requestAsAdmin(argThat(getCaseByMaterialIdQueryMatcher(materialId)))).thenReturn(buildProgressionResponse(TFL));

        boolean result = progressionProvider.isMaterialFromCPSProsecutedCase(buildAction(MATERIAL_ID_KEY, materialId.toString()));

        assertThat(result, is(false));
    }


    private Matcher<JsonEnvelope> getCaseByMaterialIdQueryMatcher(final UUID materialId) {
        return jsonEnvelope(metadata().withName("progression.query.cases-search-by-material-id"),
                payload().isJson(withJsonPath("$.q", equalTo(materialId.toString()))));
    }

    private JsonEnvelope buildProgressionResponse(final String prosecutingAuthority) {
        return envelope().withPayloadOf(prosecutingAuthority, ATTRIBUTE_PROSECUTING_AUTHORITY).build();
    }

    private Action buildAction(final String key, final String value) {
        return new Action(envelope().with(metadataWithRandomUUIDAndName()).withPayloadOf(value, key).build());
    }
}