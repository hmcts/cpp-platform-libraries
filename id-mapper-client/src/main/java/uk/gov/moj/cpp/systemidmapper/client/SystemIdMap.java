package uk.gov.moj.cpp.systemidmapper.client;


import java.util.Objects;
import java.util.UUID;

public class SystemIdMap {
    private final String sourceId;
    private final String sourceType;
    private final UUID targetId;
    private final String targetType;

    public SystemIdMap(final String sourceId, final String sourceType, final UUID targetId, final String targetType) {
        this.sourceId = sourceId;
        this.sourceType = sourceType;
        this.targetId = targetId;
        this.targetType = targetType;
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final SystemIdMap that = (SystemIdMap) o;
        return Objects.equals(getSourceId(), that.getSourceId()) &&
                Objects.equals(getSourceType(), that.getSourceType()) &&
                Objects.equals(getTargetId(), that.getTargetId()) &&
                Objects.equals(getTargetType(), that.getTargetType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSourceId(), getSourceType(), getTargetId(), getTargetType());
    }

    @Override
    public String toString() {
        return "SystemIdMap{" +
                "sourceId='" + sourceId + '\'' +
                ", sourceType='" + sourceType + '\'' +
                ", targetId=" + targetId +
                ", targetType='" + targetType + '\'' +
                '}';
    }
}
