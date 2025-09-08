package uk.gov.moj.cpp.systemidmapper.client;

import java.time.ZonedDateTime;
import java.util.UUID;

public class SystemIdMapping {
    private final UUID mappingId;
    private final String sourceId;
    private final String sourceType;
    private final UUID targetId;
    private final String targetType;
    private final ZonedDateTime createdAt;

    public SystemIdMapping(final UUID mappingId, final String sourceId, final String sourceType, final UUID targetId, final String targetType, final ZonedDateTime createdAt) {
        this.mappingId = mappingId;
        this.sourceId = sourceId;
        this.sourceType = sourceType;
        this.targetId = targetId;
        this.targetType = targetType;
        this.createdAt = createdAt;
    }

    public UUID getMappingId() {
        return mappingId;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getSourceType() {
        return sourceType;
    }

    public UUID getTargetId() {
        return targetId;
    }

    public String getTargetType() {
        return targetType;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }
}
