package uk.gov.moj.cpp.systemidmapper.client;

import java.util.Optional;
import java.util.UUID;

public interface SystemIdMapperClient {

    /**
     * Add mapping for source and target.
     *
     * @param systemIdMap the mapping of source to target ids
     * @param userId the system user id
     * @return the {@link AdditionResponse} that represents the response
     */
    AdditionResponse add(final SystemIdMap systemIdMap, final UUID userId);

    /**
     * Add mapping for source and target.
     *
     * @param systemidMapList the mapping of source to target ids
     * @param userId the system user id
     * @return the {@link AdditionResponse} that represents the response
     */
    AdditionResponses addMany(final SystemidMapList systemidMapList, final UUID userId);

    /**
     * Gets mapping by mapping id
     *
     * @param mappingId the uuid of the mapping
     * @param userId the system user id
     * @return the id mapping
     * @throws MappingNotFoundException if mapping is not found for given id
     */
    SystemIdMapping getMappingBy(final UUID mappingId, final UUID userId);

    /**
     * Finds mapping by targetId and targetType
     *
     * @param targetId the internal id
     * @param targetType the internal id type
     * @param userId the system user id
     * @return the id mapping as optional (empty if not found)
     */
    Optional<SystemIdMapping> findBy(final UUID targetId, final String targetType, final UUID userId);


    /**
     * Finds mapping by sourceId and targetTypes
     *
     * @param userId the system user id
     * @param sourceId the source identifier to search
     * @param targetTypes target types to search
     * @return the id mapping as optional (empty if not found)
     */
    Optional<SystemIdMapping> findBy(final UUID userId, final String sourceId, final String... targetTypes);

    /**
     * Finds mapping by sourceId, sourceType and targetType
     *
     * @param sourceId the id from the external system
     * @param sourceType the type of external id
     * @param targetType the internal id type
     * @param userId the system user id
     * @return the id mapping as optional (empty if not found)
     */
    Optional<SystemIdMapping> findBy(final String sourceId, final String sourceType, final String targetType, final UUID userId);

    /**
     * Updates the sourceId for a mapping with the specified mappingId
     * @param newSourceId the new source id
     * @param mappingId the id of the mapping to update
     * @param userId the current user id
     * @return the id mapping as optional (empty if not found)
     */
    Optional<SystemIdMapping> remap(final String newSourceId, final UUID mappingId, final UUID userId);
}
