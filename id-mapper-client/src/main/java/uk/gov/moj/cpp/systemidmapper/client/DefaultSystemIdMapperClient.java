package uk.gov.moj.cpp.systemidmapper.client;


import static com.jayway.jsonpath.Configuration.defaultConfiguration;
import static java.lang.String.format;
import static java.util.Optional.empty;
import static javax.json.Json.createObjectBuilder;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.Response.Status.fromStatusCode;
import static uk.gov.justice.services.common.http.HeaderConstants.USER_ID;

import uk.gov.justice.services.common.converter.JsonObjectToObjectConverter;
import uk.gov.justice.services.common.converter.StringToJsonObjectConverter;
import uk.gov.justice.services.common.converter.jackson.ObjectMapperProducer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

public class DefaultSystemIdMapperClient implements SystemIdMapperClient {

    private static final String ADD_REQUEST_MEDIA_TYPE = "application/vnd.systemid.map+json";
    private static final String REMAP_MEDIA_TYPE = "application/vnd.systemid.remap+json";
    private static final String PATH = "/mappings";
    private static final String QUERY_RESPONSE_MEDIA_TYPE = "application/vnd.systemid.mapping+json";
    private static final String UNEXPECTED_RESPONSE_CODE = "Unexpected response code";
    private static final String NEW_SOURCE_ID = "newSourceId";
    private final String baseUri;
    private static final String SOURCE_ID = "sourceId";
    private static final String SOURCE_TYPE = "sourceType";
    private static final String TARGET_ID = "targetId";
    private static final String TARGET_TYPE = "targetType";
    private static final String MAPPING_ID = "mappingId";
    private static final String COMMA = ",";
    private final ObjectMapper objectMapper;
    private final StringToJsonObjectConverter stringToJsonObjectConverter = new StringToJsonObjectConverter();
    private final JsonObjectToObjectConverter jsonObjectConverter = new JsonObjectToObjectConverter(new ObjectMapperProducer().objectMapper());
    private static final String ADD_MANY_REQUEST_MEDIA_TYPE = "application/vnd.systemid.map.list+json";

    public DefaultSystemIdMapperClient(final String baseUri, final ObjectMapper objectMapper) {
        this.baseUri = baseUri;
        this.objectMapper = objectMapper;
    }

    @Override
    public AdditionResponse add(final SystemIdMap systemIdMap, final UUID userId) {
        try (final WebTargetFactory webTargetFactory = new WebTargetFactory(baseUri, PATH)) {
            final String jsonString = objectMapper.writeValueAsString(systemIdMap);
            final Response restResponse = webTargetFactory.build()
                    .request()
                    .header(USER_ID, userId.toString())
                    .post(entity(jsonString, ADD_REQUEST_MEDIA_TYPE));

            final ResultCode resultCode = ResultCode.valueOf(restResponse.getStatus());

            final Object responseDocument = defaultConfiguration().jsonProvider().parse(restResponse.readEntity(String.class));
            final String id = JsonPath.read(responseDocument, "$.id");


            return new AdditionResponse(UUID.fromString(id), resultCode, errorMessageFrom(responseDocument, resultCode));
        } catch (final JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public AdditionResponses addMany(final SystemidMapList systemIdMapList, final UUID userId) {
        try (final WebTargetFactory webTargetFactory = new WebTargetFactory(baseUri, PATH)) {
            final String jsonString = objectMapper.writeValueAsString(systemIdMapList);
            final Response restResponse = webTargetFactory.build()
                    .request()
                    .header(USER_ID, userId.toString())
                    .post(entity(jsonString, ADD_MANY_REQUEST_MEDIA_TYPE));

            ResultCode.valueOf(restResponse.getStatus());

            final JsonObject jsonObjectPostResponse = stringToJsonObjectConverter.convert(restResponse.readEntity(String.class));
            SystemidMappingList systemidMappingList = jsonObjectConverter.convert(jsonObjectPostResponse, SystemidMappingList.class);

            return new AdditionResponses(systemidMappingList.getSystemIdMappings());
        } catch (final JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public SystemIdMapping getMappingBy(final UUID mappingId, final UUID userId) {

        try (final WebTargetFactory webTargetFactory = new WebTargetFactory(baseUri, PATH)) {
            final Response restResponse = webTargetFactory.build()
                    .path(mappingId.toString())
                    .request()
                    .header(USER_ID, userId)
                    .accept(QUERY_RESPONSE_MEDIA_TYPE)
                    .get();

            final Status statusCode = fromStatusCode(restResponse.getStatus());
            switch (statusCode) {
                case OK:
                    return systemIdMappingFrom(restResponse);
                case NOT_FOUND:
                    throw new MappingNotFoundException(format("Failed to find mapping for id %s", mappingId));
                default:
                    throw new IllegalStateException(UNEXPECTED_RESPONSE_CODE + ": " + statusCode.getStatusCode());
            }
        }
    }

    @Override
    public Optional<SystemIdMapping> findBy(final UUID targetId, final String targetType, final UUID userId) {
        try (final WebTargetFactory webTargetFactory = new WebTargetFactory(baseUri, PATH)) {
            final Response restResponse = webTargetFactory.build()
                    .queryParam(TARGET_ID, targetId.toString())
                    .queryParam(TARGET_TYPE, targetType)
                    .request()
                    .header(USER_ID, userId)
                    .accept(QUERY_RESPONSE_MEDIA_TYPE)
                    .get();


            return systemMappingFrom(restResponse);
        }
    }


    /**
     * Query from system.id-mapper-api with sourceId and targetTypes.
     * It sends concatenating of targetTypes with comma to system.id-mapper-api
     *
     * @param userId
     * @param sourceId
     * @param targetTypes
     * @return SystemIdMapping
     */
    @Override
    public Optional<SystemIdMapping> findBy(final UUID userId, final String sourceId, final String... targetTypes) {
        try (final WebTargetFactory webTargetFactory = new WebTargetFactory(baseUri, PATH)) {
            final Response restResponse = webTargetFactory.build()
                    .queryParam(SOURCE_ID, sourceId)
                    .queryParam(TARGET_TYPE, Arrays.asList(targetTypes).stream().collect(Collectors.joining(COMMA)))
                    .request()
                    .header(USER_ID, userId)
                    .accept(QUERY_RESPONSE_MEDIA_TYPE)
                    .get();


            return systemMappingFrom(restResponse);
        }
    }

    @Override
    public Optional<SystemIdMapping> findBy(final String sourceId, final String sourceType, final String targetType, final UUID userId) {
        try (final WebTargetFactory webTargetFactory = new WebTargetFactory(baseUri, PATH)) {
            final Response restResponse = webTargetFactory.build()
                    .queryParam(SOURCE_ID, sourceId)
                    .queryParam(SOURCE_TYPE, sourceType)
                    .queryParam(TARGET_TYPE, targetType)
                    .request()
                    .header(USER_ID, userId)
                    .accept(QUERY_RESPONSE_MEDIA_TYPE)
                    .get();

            return systemMappingFrom(restResponse);
        }
    }

    @Override
    public Optional<SystemIdMapping> remap(final String newSourceId, final UUID mappingId, final UUID userId) {

        final String jsonString = createObjectBuilder()
                .add(NEW_SOURCE_ID, newSourceId)
                .add(MAPPING_ID, mappingId.toString())
                .build()
                .toString();

        try (final WebTargetFactory webTargetFactory = new WebTargetFactory(baseUri, PATH)) {
            final Response restResponse = webTargetFactory.build()
                    .request()
                    .header(USER_ID, userId.toString())
                    .post(entity(jsonString, REMAP_MEDIA_TYPE));

            return systemMappingFrom(restResponse);

        }
    }

    private Optional<SystemIdMapping> systemMappingFrom(final Response restResponse) {

        final Status statusCode = fromStatusCode(restResponse.getStatus());

        switch (statusCode) {
            case OK:
                return Optional.of(systemIdMappingFrom(restResponse));
            case NOT_FOUND:
                return empty();
            default:
                throw new IllegalStateException(UNEXPECTED_RESPONSE_CODE + ": " + statusCode.getStatusCode());
        }
    }

    private SystemIdMapping systemIdMappingFrom(final Response restResponse) {
        try {
            return objectMapper.readValue(restResponse.readEntity(String.class), SystemIdMapping.class);
        } catch (final IOException e) {
            throw new IllegalStateException("Malformed response body", e);
        }
    }

    private Optional<String> errorMessageFrom(final Object responseDocument, final ResultCode code) {
        return code == ResultCode.OK ? empty() : Optional.of(JsonPath.read(responseDocument, "$.error"));
    }
}
