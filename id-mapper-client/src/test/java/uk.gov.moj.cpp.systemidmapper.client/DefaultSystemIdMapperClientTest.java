package uk.gov.moj.cpp.systemidmapper.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.UUID.randomUUID;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;
import static org.apache.openejb.util.NetworkUtil.getNextAvailablePort;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import uk.gov.justice.services.common.converter.JsonObjectToObjectConverter;
import uk.gov.justice.services.common.converter.jackson.ObjectMapperProducer;
import uk.gov.justice.services.common.util.UtcClock;
import uk.gov.justice.services.test.utils.framework.api.JsonObjectConvertersFactory;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import javax.json.JsonObject;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;

@WireMockTest(httpPort = 21789)
public class DefaultSystemIdMapperClientTest {
    private static final int PORT = 21789;

    private static final String MAPPER_BASE_URI = format("http://localhost:%d/system-id-mapper", PORT);
    private static final String NOT_USED_SOURCE_ID = "1";
    private static final String NOT_USED_SOURCE_TYPE = "t";
    private static final UUID NOT_USED_TARGET_ID = randomUUID();
    private static final String NOT_USED_TARGET_TYPE = "tt";
    private static final String MAPPINGS_URL = "/system-id-mapper/mappings";

    private final DefaultSystemIdMapperClient client = new DefaultSystemIdMapperClient(MAPPER_BASE_URI, new ObjectMapperProducer().objectMapper());
    private final JsonObjectToObjectConverter jsonObjectConverter = new JsonObjectConvertersFactory().jsonObjectToObjectConverter();

    @Test
    public void shouldPostNewMapping() throws Exception {

        stubFor(post(urlPathEqualTo(MAPPINGS_URL))
                .willReturn(aResponse()
                        .withStatus(OK.getStatusCode())
                        .withBody("{ \"id\": \"266c0ae9-e276-4d29-b669-cb32013228b4\",\n}")));

        final String sourceId = "sourceIdAbc";
        final String sourceType = "sourceIdTypeBCD";
        final UUID targetId = randomUUID();
        final String targetType = "targetIdTypeEFG";
        final UUID userId = randomUUID();

        final SystemIdMap systemIdMap = new SystemIdMap(sourceId, sourceType, targetId, targetType);

        client.add(systemIdMap, userId);

        verify(postRequestedFor(urlPathEqualTo(MAPPINGS_URL))
                .withRequestBody(equalToJson(mapJsonOf(sourceId, sourceType, targetId, targetType))));

    }

    @Test
    public void shouldReturnOKResponseWhenAddingNewMapping() {
        final String mappingId = "166c0ae9-e276-4d29-b669-cb32013228b3";
        final UUID userId = randomUUID();
        stubFor(post(urlPathEqualTo(MAPPINGS_URL))
                .willReturn(aResponse()
                        .withStatus(OK.getStatusCode())
                        .withBody("{ \"id\": \"" + mappingId + "\"}")));

        final SystemIdMap systemIdMap = new SystemIdMap(NOT_USED_SOURCE_ID, NOT_USED_SOURCE_TYPE, NOT_USED_TARGET_ID, NOT_USED_TARGET_TYPE);
        final AdditionResponse response = client.add(systemIdMap, userId);

        assertThat(response.code(), is(ResultCode.OK));
        assertThat(response.mappingId(), is(UUID.fromString(mappingId)));
        assertThat(response.errorMessage(), is(empty()));

    }

    @Test
    public void shouldReturnConflictResponseWhenAddingNewMapping() {
        final String mappingId = "166c0ae9-e276-4d29-b669-cb32013228b5";
        final UUID userId = randomUUID();
        final String errorMessage = "some conflict occurred";
        stubFor(post(urlPathEqualTo(MAPPINGS_URL))
                .willReturn(aResponse()
                        .withStatus(CONFLICT.getStatusCode())
                        .withBody("{ \"id\": \"" + mappingId + "\",\"error\": \"" + errorMessage + "\"}")));

        final SystemIdMap systemIdMap = new SystemIdMap(NOT_USED_SOURCE_ID, NOT_USED_SOURCE_TYPE, NOT_USED_TARGET_ID, NOT_USED_TARGET_TYPE);
        final AdditionResponse response = client.add(systemIdMap, userId);

        assertThat(response.code(), is(ResultCode.CONFLICT));
        assertThat(response.mappingId(), is(UUID.fromString(mappingId)));
        assertThat(response.errorMessage().get(), is(errorMessage));

    }

    @Test
    public void shouldThrowExceptionForUnexpectedHttpCodeWhenAddingNewMapping() throws Exception {

        final UUID userId = randomUUID();
        stubFor(post(urlPathEqualTo(MAPPINGS_URL))
                .willReturn(aResponse().withStatus(SERVICE_UNAVAILABLE.getStatusCode())));

        final SystemIdMap systemIdMap = new SystemIdMap(NOT_USED_SOURCE_ID, NOT_USED_SOURCE_TYPE, NOT_USED_TARGET_ID, NOT_USED_TARGET_TYPE);

        final IllegalStateException illegalStateException = assertThrows(IllegalStateException.class, () -> client.add(systemIdMap, userId));

        assertThat(illegalStateException.getMessage(), is("Unexpected response code: 503"));
    }

    @Test
    public void shouldGetMappingByMappingId() throws Exception {
        final UUID mappingId = randomUUID();
        final String sourceId = "sourceIdAbc";
        final String sourceType = "sourceIdTypeBCD";
        final UUID targetId = randomUUID();
        final String targetType = "targetIdTypeEFG";
        final String zonedDateTime = "2016-09-07T14:30:53.0Z";
        final UUID userId = randomUUID();

        stubFor(get(urlPathEqualTo(MAPPINGS_URL + "/" + mappingId))
                .willReturn(aResponse()
                        .withStatus(OK.getStatusCode())
                        .withBody(mappingJsonOf(mappingId, sourceId, sourceType, targetId, targetType, zonedDateTime))));

        final SystemIdMapping systemIdMap = client.getMappingBy(mappingId, userId);

        assertThat(systemIdMap.getMappingId(), is(mappingId));
        assertThat(systemIdMap.getSourceId(), is(sourceId));
        assertThat(systemIdMap.getSourceType(), is(sourceType));
        assertThat(systemIdMap.getTargetId(), is(targetId));
        assertThat(systemIdMap.getTargetType(), is(targetType));
        assertThat(systemIdMap.getCreatedAt(), is(ZonedDateTime.of(2016, 9, 7, 14, 30, 53, 0, ZoneId.of("UTC"))));
    }

    @Test
    public void shouldThrowExceptionIfResponseCodeIsNotExpected() throws Exception {
        final UUID mappingId = randomUUID();
        final UUID userId = randomUUID();

        stubFor(get(urlPathEqualTo(MAPPINGS_URL + "/" + mappingId))
                .willReturn(aResponse()
                        .withStatus(BAD_REQUEST.getStatusCode())));


        final IllegalStateException illegalStateException = assertThrows(IllegalStateException.class, () -> client.getMappingBy(mappingId, userId));

        assertThat(illegalStateException.getMessage(), is("Unexpected response code: 400"));
    }

    @Test
    public void shouldThrowExceptionIfResponseContainsIncorrectJson() throws Exception {
        final UUID mappingId = randomUUID();
        final UUID userId = randomUUID();

        stubFor(get(urlPathEqualTo(MAPPINGS_URL + "/" + mappingId))
                .willReturn(aResponse()
                        .withStatus(OK.getStatusCode())));

        final IllegalStateException illegalStateException = assertThrows(IllegalStateException.class, () -> client.getMappingBy(mappingId, userId));

        assertThat(illegalStateException.getMessage(), is("Malformed response body"));
    }

    @Test
    public void shouldThrowExceptionIfMappingNotFoundById() throws Exception {
        final UUID mappingId = randomUUID();
        final UUID userId = randomUUID();

        stubFor(get(urlPathEqualTo(MAPPINGS_URL + "/" + mappingId))
                .willReturn(aResponse()
                        .withStatus(NOT_FOUND.getStatusCode())));

        final MappingNotFoundException mappingNotFoundException = assertThrows(MappingNotFoundException.class, () -> client.getMappingBy(mappingId, userId));

        assertThat(mappingNotFoundException.getMessage(), is((format("Failed to find mapping for id %s", mappingId))));
    }

    @Test
    public void shouldQueryByTargetIdAndTargetType() throws Exception {

        final UUID userId = randomUUID();
        final String sourceId = "sourceIdAbc";
        final String sourceType = "sourceIdTypeBCD";
        final UUID targetId = randomUUID();
        final String targetType = "someType";

        stubFor(get(urlPathEqualTo(MAPPINGS_URL))
                .willReturn(aResponse()
                        .withStatus(OK.getStatusCode())
                        .withBody(mapJsonOf(sourceId, sourceType, targetId, targetType))));

        final Optional<SystemIdMapping> systemIdMapping = client.findBy(targetId, targetType, userId);

        if (systemIdMapping.isPresent()) {
            assertThat(systemIdMapping.get().getSourceId(), is(sourceId));
            assertThat(systemIdMapping.get().getSourceType(), is(sourceType));
            assertThat(systemIdMapping.get().getTargetId(), is(targetId));
            assertThat(systemIdMapping.get().getTargetType(), is(targetType));
        } else {
            fail();
        }
        verify(getRequestedFor(urlPathEqualTo(MAPPINGS_URL))
                .withHeader(ACCEPT, equalTo("application/vnd.systemid.mapping+json"))
                .withQueryParam("targetId", equalTo(targetId.toString()))
                .withQueryParam("targetType", equalTo(targetType))
        );
    }

    @Test
    public void shouldFindMappingByTargetIdAndTargetType() throws Exception {
        final UUID userId = randomUUID();
        final UUID targetId = randomUUID();
        final String targetType = "someType";

        final UUID mappingId = randomUUID();
        final String sourceId = "sourceIdAbc";
        final String sourceType = "sourceIdTypeBCD";
        final String zonedDateTime = "2016-10-08T15:31:54.0Z";


        stubFor(get(urlPathEqualTo(MAPPINGS_URL))
                .withQueryParam("targetId", equalTo(targetId.toString()))
                .withQueryParam("targetType", equalTo(targetType))
                .willReturn(aResponse()
                        .withStatus(OK.getStatusCode())
                        .withBody(mappingJsonOf(mappingId, sourceId, sourceType, targetId, targetType, zonedDateTime))));


        final Optional<SystemIdMapping> systemIdMapOptional = client.findBy(targetId, targetType, userId);
        assertThat(systemIdMapOptional.isPresent(), is(true));
        final SystemIdMapping systemIdMap = systemIdMapOptional.get();
        assertThat(systemIdMap.getMappingId(), is(mappingId));
        assertThat(systemIdMap.getSourceId(), is(sourceId));
        assertThat(systemIdMap.getSourceType(), is(sourceType));
        assertThat(systemIdMap.getTargetId(), is(targetId));
        assertThat(systemIdMap.getTargetType(), is(targetType));
        assertThat(systemIdMap.getCreatedAt(), is(ZonedDateTime.of(2016, 10, 8, 15, 31, 54, 0, ZoneId.of("UTC"))));

    }

    @Test
    public void shouldReturnEmptyWhenMappingNotFoundByTargetIdAndTargetType() {
        final UUID userId = randomUUID();
        final UUID targetId = randomUUID();
        final String targetType = "someType2";

        stubFor(get(urlPathEqualTo(MAPPINGS_URL))
                .withQueryParam("targetId", equalTo(targetId.toString()))
                .withQueryParam("targetType", equalTo(targetType))
                .willReturn(aResponse()
                        .withStatus(NOT_FOUND.getStatusCode())));

        assertThat(client.findBy(targetId, targetType, userId), is(empty()));
    }

    @Test
    public void shouldThrowExceptionWhenUnexpectedStatusCodeWhenSearchingByTargetIdAndTargetType() {

        final UUID userId = randomUUID();
        final UUID targetId = randomUUID();
        final String targetType = "someType3";

        stubFor(get(urlPathEqualTo(MAPPINGS_URL))
                .withQueryParam("targetId", equalTo(targetId.toString()))
                .withQueryParam("targetType", equalTo(targetType))
                .willReturn(aResponse()
                        .withStatus(SERVICE_UNAVAILABLE.getStatusCode())));

        final IllegalStateException illegalStateException = assertThrows(IllegalStateException.class, () -> client.findBy(targetId, targetType, userId));

        assertThat(illegalStateException.getMessage(), is(("Unexpected response code: 503")));
    }

    @Test
    public void shouldFindMappingBySourceIdSourceTypeAndTargetType() {

        final UUID userId = randomUUID();
        final UUID targetId = randomUUID();
        final String targetType = "someType3";

        final UUID mappingId = randomUUID();
        final String sourceId = "sourceIddef";
        final String sourceType = "sourceIdTypeEFG";
        final String zonedDateTime = "2015-09-07T14:30:53.0Z";

        stubFor(get(urlPathEqualTo(MAPPINGS_URL))
                .withQueryParam("sourceId", equalTo(sourceId))
                .withQueryParam("sourceType", equalTo(sourceType))
                .withQueryParam("targetType", equalTo(targetType))
                .willReturn(aResponse()
                        .withStatus(OK.getStatusCode())
                        .withBody(mappingJsonOf(mappingId, sourceId, sourceType, targetId, targetType, zonedDateTime))));

        final Optional<SystemIdMapping> systemIdMapOptional = client.findBy(sourceId, sourceType, targetType, userId);

        assertThat(systemIdMapOptional.isPresent(), is(true));
        final SystemIdMapping systemIdMap = systemIdMapOptional.get();
        assertThat(systemIdMap.getMappingId(), is(mappingId));
        assertThat(systemIdMap.getSourceId(), is(sourceId));
        assertThat(systemIdMap.getSourceType(), is(sourceType));
        assertThat(systemIdMap.getTargetId(), is(targetId));
        assertThat(systemIdMap.getTargetType(), is(targetType));
        assertThat(systemIdMap.getCreatedAt(), is(ZonedDateTime.of(2015, 9, 7, 14, 30, 53, 0, ZoneId.of("UTC"))));

    }


    @Test
    public void shouldReturnEmptyWhenMappingNotFoundBySourceIdSourceTypeAndTargetType() {

        final UUID userId = randomUUID();
        final String targetType = "someType2";
        final String sourceId = "sourceIddef";
        final String sourceType = "sourceIdTypeEFG";

        stubFor(get(urlPathEqualTo(MAPPINGS_URL))
                .withQueryParam("sourceId", equalTo(sourceId))
                .withQueryParam("sourceType", equalTo(sourceType))
                .withQueryParam("targetType", equalTo(targetType))
                .willReturn(aResponse()
                        .withStatus(NOT_FOUND.getStatusCode())));

        assertThat(client.findBy(sourceId, sourceType, targetType, userId), is(empty()));
    }

    @Test
    public void shouldThrowExceptionWhenUnexpectedStatusCodeWhenSearchingBySourceIdSourceTypeAndTargetType() {

        final String targetType = "someType4";
        final String sourceId = "sourceId4";
        final String sourceType = "sourceIdTypeEFGH";

        final UUID userId = randomUUID();

        stubFor(get(urlPathEqualTo(MAPPINGS_URL))
                .withQueryParam("sourceId", equalTo(sourceId))
                .withQueryParam("sourceType", equalTo(sourceType))
                .withQueryParam("targetType", equalTo(targetType))
                .willReturn(aResponse()
                        .withStatus(SERVICE_UNAVAILABLE.getStatusCode())));

        final IllegalStateException illegalStateException = assertThrows(IllegalStateException.class, () -> client.findBy(sourceId, sourceType, targetType, userId));

        assertThat(illegalStateException.getMessage(), is(("Unexpected response code: 503")));
    }

    @Test
    public void shouldFindMappingBySourceIdAndTargetType() {
        final UUID userId = randomUUID();
        final UUID targetId = randomUUID();
        final UUID mappingId = randomUUID();

        final String targetType1 = "CASE-ID";
        final String sourceId = "GAFTL00C2AAACD3455";
        final String sourceType = "MCC-REF";
        final String zonedDateTime = "2019-10-07T14:30:53.0Z";

        stubFor(get(urlPathEqualTo(MAPPINGS_URL))
                .withQueryParam("sourceId", equalTo(sourceId))
                .withQueryParam("targetType", equalTo(targetType1))
                .willReturn(aResponse()
                        .withStatus(OK.getStatusCode())
                        .withBody(mappingJsonOf(mappingId, sourceId, sourceType, targetId, targetType1, zonedDateTime))));

        final Optional<SystemIdMapping> systemIdMapOptional = client.findBy(userId, sourceId, targetType1);

        assertThat(systemIdMapOptional.isPresent(), is(true));
        final SystemIdMapping systemIdMap = systemIdMapOptional.get();
        assertThat(systemIdMap.getMappingId(), is(mappingId));
        assertThat(systemIdMap.getSourceId(), is(sourceId));
        assertThat(systemIdMap.getSourceType(), is(sourceType));
        assertThat(systemIdMap.getTargetId(), is(targetId));
        assertThat(systemIdMap.getTargetType(), is("CASE-ID"));
        assertThat(systemIdMap.getCreatedAt(), is(ZonedDateTime.of(2019, 10, 7, 14, 30, 53, 0, ZoneId.of("UTC"))));
    }


    @Test
    public void shouldFindMappingBySourceIdAndMultipleTargetType() {
        final UUID userId = randomUUID();
        final UUID targetId = randomUUID();
        final UUID mappingId = randomUUID();

        final String targetType1 = "CASE-ID";
        final String targetType2 = "CASE_FILE_ID";
        final String sourceId = "GAFTL00C2AAACD3455";
        final String sourceType = "MCC-REF";
        final String zonedDateTime = "2019-10-07T14:30:53.0Z";

        stubFor(get(urlPathEqualTo(MAPPINGS_URL))
                .withQueryParam("sourceId", equalTo(sourceId))
                .withQueryParam("targetType", containing(targetType1))
                .withQueryParam("targetType", containing(targetType2))
                .willReturn(aResponse()
                        .withStatus(OK.getStatusCode())
                        .withBody(mappingJsonOf(mappingId, sourceId, sourceType, targetId, targetType1, zonedDateTime))));

        final Optional<SystemIdMapping> systemIdMapOptional = client.findBy(userId, sourceId, targetType1, targetType2);

        assertThat(systemIdMapOptional.isPresent(), is(true));
        final SystemIdMapping systemIdMap = systemIdMapOptional.get();
        assertThat(systemIdMap.getMappingId(), is(mappingId));
        assertThat(systemIdMap.getSourceId(), is(sourceId));
        assertThat(systemIdMap.getSourceType(), is(sourceType));
        assertThat(systemIdMap.getTargetId(), is(targetId));
        assertThat(systemIdMap.getTargetType(), is("CASE-ID"));
        assertThat(systemIdMap.getCreatedAt(), is(ZonedDateTime.of(2019, 10, 7, 14, 30, 53, 0, ZoneId.of("UTC"))));
    }

    @Test
    public void shouldReturnEmptyWhenMappingNotFoundBySourceIdAndTargetType() {
        final UUID userId = randomUUID();
        final String targetType = "CASE-ID";
        final String sourceId = "GAFTL00C2AAACD3455";

        stubFor(get(urlPathEqualTo(MAPPINGS_URL))
                .withQueryParam("sourceId", equalTo(sourceId))
                .withQueryParam("targetType", equalTo(targetType))
                .willReturn(aResponse()
                        .withStatus(NOT_FOUND.getStatusCode())));

        assertThat(client.findBy(userId, sourceId, targetType), is(empty()));
    }


    @Test
    public void shouldThrowExceptionWhenUnexpectedStatusCodeWhenSearchingBySourceIdAndTargetType() {
        final UUID userId = randomUUID();
        final String targetType = "targetType";
        final String sourceId = "sourceId";

        stubFor(get(urlPathEqualTo(MAPPINGS_URL))
                .withQueryParam("sourceId", equalTo(sourceId))
                .withQueryParam("targetType", equalTo(targetType))
                .willReturn(aResponse()
                        .withStatus(SERVICE_UNAVAILABLE.getStatusCode())));

        final IllegalStateException illegalStateException = assertThrows(IllegalStateException.class, () -> client.findBy(userId, sourceId, targetType));

        assertThat(illegalStateException.getMessage(), is(("Unexpected response code: 503")));
    }

    @Test
    public void shouldRemapASystemIdMapping() throws Exception {

        final String newSourceId = "new-source-id";
        final UUID mappingId = randomUUID();
        final UUID userId = randomUUID();

        final String sourceType = "source-type";
        final UUID targetId = randomUUID();
        final String targetType = "target-type";
        final ZonedDateTime createdAt = new UtcClock().now();

        final String responseBody = mappingJsonOf(
                mappingId,
                newSourceId,
                sourceType,
                targetId,
                targetType,
                createdAt.toString()
        );

        stubFor(post(urlPathEqualTo(MAPPINGS_URL))
                .withHeader("Content-Type", equalTo("application/vnd.systemid.remap+json"))
                .willReturn(aResponse()
                        .withStatus(OK.getStatusCode())
                        .withBody(responseBody)));

        final Optional<SystemIdMapping> systemIdMapping = client.remap(newSourceId, mappingId, userId);

        if (!systemIdMapping.isPresent()) {
            fail();
        }

        assertThat(systemIdMapping.get().getMappingId(), is(mappingId));
        assertThat(systemIdMapping.get().getSourceId(), is(newSourceId));
        assertThat(systemIdMapping.get().getSourceType(), is(sourceType));
        assertThat(systemIdMapping.get().getTargetId(), is(targetId));
        assertThat(systemIdMapping.get().getTargetType(), is(targetType));
        assertThat(systemIdMapping.get().getCreatedAt().toInstant(), is(createdAt.toInstant()));
    }

    @Test
    public void shouldPostNewMappings() {
        final UUID userId = randomUUID();
        final JsonObject addManyResponse = FileUtil.givenPayload("/test-data/systemid.mapping.list-response.json");

        stubFor(post(urlPathEqualTo(MAPPINGS_URL))
                .willReturn(aResponse()
                        .withStatus(OK.getStatusCode())
                        .withBody(addManyResponse.toString())));

        final JsonObject systemIdMapListJson = FileUtil.givenPayload("/test-data/systemid.map.list.json");
        final SystemidMapList systemidMapList = jsonObjectConverter.convert(systemIdMapListJson, SystemidMapList.class);

        final AdditionResponses additionResponses = client.addMany(systemidMapList, userId);

        assertThat(additionResponses.getSystemIdMappings().size(), is(2));
        assertThat(additionResponses.getSystemIdMappings().stream().allMatch(systemIdMappings -> systemIdMappings.getIsError() == false), is(true));
    }

    private String mappingJsonOf(final UUID mappingId, final String sourceId, final String sourceType, final UUID targetId, final String targetType, final String createdAt) {
        return "{\n" +
                "  \"mappingId\": \"" + mappingId.toString() + "\",\n" +
                "  \"sourceId\": \"" + sourceId + "\",\n" +
                "  \"sourceType\": \"" + sourceType + "\",\n" +
                "  \"targetId\":\"" + targetId.toString() + "\",\n" +
                "  \"targetType\":\"" + targetType + "\",\n" +
                "  \"createdAt\":\"" + createdAt + "\"\n" +
                "}";
    }

    private String mapJsonOf(final String sourceId, final String sourceType, final UUID targetId, final String targetType) {
        return "{\n" +
                "  \"sourceId\": \"" + sourceId + "\",\n" +
                "  \"sourceType\": \"" + sourceType + "\",\n" +
                "  \"targetId\":\"" + targetId.toString() + "\",\n" +
                "  \"targetType\":\"" + targetType + "\"\n" +
                "}";
    }
}
