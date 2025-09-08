package uk.gov.moj.cpp.systemidmapper.client;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

public class SystemidMappingList implements Serializable {
  private static final long serialVersionUID = -8487021779307318620L;

  private final List<SystemIdMappings> systemIdMappings;

  @JsonCreator
  public SystemidMappingList(final List<SystemIdMappings> systemIdMappings) {
    this.systemIdMappings = systemIdMappings;
  }

  public List<SystemIdMappings> getSystemIdMappings() {
    return systemIdMappings;
  }

  public static Builder systemidMappingList() {
    return new Builder();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    final SystemidMappingList that = (SystemidMappingList) obj;

    return java.util.Objects.equals(this.systemIdMappings, that.systemIdMappings);
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(systemIdMappings);}

  public static class Builder {
    private List<SystemIdMappings> systemIdMappings;

    public Builder withSystemIdMappings(final List<SystemIdMappings> systemIdMappings) {
      this.systemIdMappings = systemIdMappings;
      return this;
    }

    public Builder withValuesFrom(final SystemidMappingList systemidMappingList) {
      this.systemIdMappings = systemidMappingList.getSystemIdMappings();
      return this;
    }

    public SystemidMappingList build() {
      return new SystemidMappingList(systemIdMappings);
    }
  }
}
