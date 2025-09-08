package uk.gov.moj.cpp.systemidmapper.client;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

public class SystemidMapList implements Serializable {
  private static final long serialVersionUID = 3644770508049488940L;

  private final List<SystemIdMap> systemIds;

  @JsonCreator
  public SystemidMapList(final List<SystemIdMap> systemIds) {
    this.systemIds = systemIds;
  }

  public List<SystemIdMap> getSystemIds() {
    return systemIds;
  }

  public static Builder systemidMapList() {
    return new Builder();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    final SystemidMapList that = (SystemidMapList) obj;

    return java.util.Objects.equals(this.systemIds, that.systemIds);
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(systemIds);}

  public static class Builder {
    private List<SystemIdMap> systemIds;

    public Builder withSystemIds(final List<SystemIdMap> systemIds) {
      this.systemIds = systemIds;
      return this;
    }

    public Builder withValuesFrom(final SystemidMapList systemidMapList) {
      this.systemIds = systemidMapList.getSystemIds();
      return this;
    }

    public SystemidMapList build() {
      return new SystemidMapList(systemIds);
    }
  }
}
