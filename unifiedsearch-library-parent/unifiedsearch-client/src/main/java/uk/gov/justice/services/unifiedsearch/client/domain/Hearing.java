package uk.gov.justice.services.unifiedsearch.client.domain;


import java.util.List;
import java.util.Objects;

public class Hearing {

    private String hearingId;
    private String courtId;
    private String courtCentreName;
    private String hearingTypeId;
    private String hearingTypeLabel ;
    private List<String> hearingDates;
    private List<HearingDay> hearingDays;
    private String jurisdictionType ;
    private List<String> judiciaryTypes;
    private List<String> defendantIds;
    private List<DefenceCounsel> defenceCounsels;

    private boolean isBoxHearing;
    private Boolean isVirtualBoxHearing;

    private String courtCentreRoomId;
    private String courtCentreRoomName;
    private String courtCentreWelshName;
    private String courtCentreRoomWelshName;
    private Address courtCentreAddress;
    private String hearingTypeCode;
    private String courtCentreCode;
    private String estimatedDuration;

    public String getHearingId() {
        return hearingId;
    }

    public void setHearingId(final String hearingId) {
        this.hearingId = hearingId;
    }

    public String getCourtId() {
        return courtId;
    }

    public void setCourtId(final String courtId) {
        this.courtId = courtId;
    }

    public String getHearingTypeId() {
        return hearingTypeId;
    }

    public void setHearingTypeId(final String hearingTypeId) {
        this.hearingTypeId = hearingTypeId;
    }

    public String getHearingTypeLabel() {
        return hearingTypeLabel;
    }

    public void setHearingTypeLabel(final String hearingTypeLabel) {
        this.hearingTypeLabel = hearingTypeLabel;
    }

    public String getCourtCentreName() {
        return courtCentreName;
    }

    public void setCourtCentreName(final String courtCentreName) {
        this.courtCentreName = courtCentreName;
    }

    public List<HearingDay> getHearingDays() {
        return hearingDays;
    }

    public void setHearingDays(final List<HearingDay> hearingDays) {
        this.hearingDays = hearingDays;
    }

    public String getJurisdictionType() {
        return jurisdictionType;
    }

    public void setJurisdictionType(final String jurisdictionType) {
        this.jurisdictionType = jurisdictionType;
    }

    public List<String> getJudiciaryTypes() {
        return judiciaryTypes;
    }

    public void setJudiciaryTypes(final List<String> judiciaryTypes) {
        this.judiciaryTypes = judiciaryTypes;
    }

    public List<String> getDefendantIds() {
        return defendantIds;
    }

    public Hearing setDefendantIds(final List<String> defendantIds) {
        this.defendantIds = defendantIds;
        return this;
    }

    public boolean isIsBoxHearing() {
        return isBoxHearing;
    }

    public Hearing setIsIsBoxHearing(final boolean boxHearing) {
        isBoxHearing = boxHearing;
        return this;
    }

    public boolean isIsVirtualBoxHearing() {
        return isVirtualBoxHearing;
    }

    public Hearing setIsIsVirtualBoxHearing(final boolean virtualBoxHearing) {
        isVirtualBoxHearing = virtualBoxHearing;
        return this;
    }

    public String getCourtCentreRoomId() {
        return courtCentreRoomId;
    }

    public void setCourtCentreRoomId(final String courtCentreRoomId) {
        this.courtCentreRoomId = courtCentreRoomId;
    }

    public String getCourtCentreRoomName() {
        return courtCentreRoomName;
    }

    public void setCourtCentreRoomName(final String courtCentreRoomName) {
        this.courtCentreRoomName = courtCentreRoomName;
    }

    public String getCourtCentreWelshName() {
        return courtCentreWelshName;
    }

    public void setCourtCentreWelshName(final String courtCentreWelshName) {
        this.courtCentreWelshName = courtCentreWelshName;
    }

    public String getCourtCentreRoomWelshName() {
        return courtCentreRoomWelshName;
    }

    public void setCourtCentreRoomWelshName(final String courtCentreRoomWelshName) {
        this.courtCentreRoomWelshName = courtCentreRoomWelshName;
    }

    public Address getCourtCentreAddress() {
        return courtCentreAddress;
    }

    public void setCourtCentreAddress(final Address courtCentreAddress) {
        this.courtCentreAddress = courtCentreAddress;
    }

    public String getHearingTypeCode() {
        return hearingTypeCode;
    }

    public void setHearingTypeCode(final String hearingTypeCode) {
        this.hearingTypeCode = hearingTypeCode;
    }

    public List<String> getHearingDates() {
        return hearingDates;
    }

    public Hearing setHearingDates(final List<String> hearingDates) {
        this.hearingDates = hearingDates;
        return this;
    }

    public String getCourtCentreCode() {
        return courtCentreCode;
    }

    public void setCourtCentreCode(final String courtCentreCode) {
        this.courtCentreCode = courtCentreCode;
    }

    public String getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(final String estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public List<DefenceCounsel> getDefenceCounsels() {
        return defenceCounsels;
    }

    public Hearing setDefenceCounsels(final List<DefenceCounsel> defenceCounsels) {
        this.defenceCounsels = defenceCounsels;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Hearing hearing = (Hearing) o;
        return Objects.equals(hearingId, hearing.hearingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hearingId);
    }
}
