package uk.gov.moj.cpp.systemidmapper.client;

import java.io.Serializable;
import java.util.UUID;

public class SystemIdMappings implements Serializable {
  private static final long serialVersionUID = -8487021779307318620L;

  private final String error;

  private final Boolean isError;

  private final UUID mappingId;

  private final String sourceId;

  private final UUID targetId;

  public SystemIdMappings(final String error, final Boolean isError, final UUID mappingId, final String sourceId, final UUID targetId) {
    this.error = error;
    this.isError = isError;
    this.mappingId = mappingId;
    this.sourceId = sourceId;
    this.targetId = targetId;
  }

  public String getError() {
    return error;
  }

  public Boolean getIsError() {
    return isError;
  }

  public UUID getMappingId() {
    return mappingId;
  }

  public String getSourceId() {
    return sourceId;
  }

  public UUID getTargetId() {
    return targetId;
  }

  public static Builder systemIdMappings() {
    return new Builder();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    final SystemIdMappings that = (SystemIdMappings) obj;

    return java.util.Objects.equals(this.error, that.error) &&
    java.util.Objects.equals(this.isError, that.isError) &&
    java.util.Objects.equals(this.mappingId, that.mappingId) &&
    java.util.Objects.equals(this.sourceId, that.sourceId) &&
    java.util.Objects.equals(this.targetId, that.targetId);
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(error, isError, mappingId, sourceId, targetId);}

  public static class Builder {
    private String error;

    private Boolean isError;

    private UUID mappingId;

    private String sourceId;

    private UUID targetId;

    public Builder withError(final String error) {
      this.error = error;
      return this;
    }

    public Builder withIsError(final Boolean isError) {
      this.isError = isError;
      return this;
    }

    public Builder withMappingId(final UUID mappingId) {
      this.mappingId = mappingId;
      return this;
    }

    public Builder withSourceId(final String sourceId) {
      this.sourceId = sourceId;
      return this;
    }

    public Builder withTargetId(final UUID targetId) {
      this.targetId = targetId;
      return this;
    }

    public Builder withValuesFrom(final SystemIdMappings systemIdMappings) {
      this.error = systemIdMappings.getError();
      this.isError = systemIdMappings.getIsError();
      this.mappingId = systemIdMappings.getMappingId();
      this.sourceId = systemIdMappings.getSourceId();
      this.targetId = systemIdMappings.getTargetId();
      return this;
    }

    public SystemIdMappings build() {
      return new SystemIdMappings(error, isError, mappingId, sourceId, targetId);
    }
  }
}
