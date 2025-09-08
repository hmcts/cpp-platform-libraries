package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document;

import java.util.List;
import java.util.stream.Collectors;

public class HearingDocument {

    private String hearingId;
    private String courtId;
    private String courtCentreName;
    private String hearingTypeId;
    private String hearingTypeLabel;
    private List<String> hearingDates;
    private List<HearingDayDocument> hearingDays;
    private String jurisdictionType;
    private List<String> judiciaryTypes;
    private List<String> defendantIds;
    private List<DefenceCounselDocument> defenceCounsels;

    private Boolean isBoxHearing;
    private Boolean isVirtualBoxHearing;

    private String courtCentreRoomId;
    private String courtCentreRoomName;
    private String courtCentreWelshName;
    private String courtCentreRoomWelshName;
    private AddressDocument courtCentreAddress;
    private String hearingTypeCode;
    private String courtCentreCode;
    private String estimatedDuration;

    private HearingDocument() {
    }


    public HearingDocument(final String hearingId, final String courtId, final String courtCentreName, final String hearingTypeId,
                           final String hearingTypeLabel, final List<String> hearingDates, final List<HearingDayDocument> hearingDays, final String jurisdictionType,
                           final List<String> judiciaryTypes, final Boolean isBoxHearing, final String courtCentreRoomId, final String courtCentreRoomName,
                           final String courtCentreWelshName, final String courtCentreRoomWelshName, final AddressDocument courtCentreAddress, final String hearingTypeCode,
                           final List<String> defendantIds, final String courtCentreCode, final Boolean isVirtualBoxHearing, final String estimatedDuration, final List<DefenceCounselDocument> defenceCounsels) {

        this.hearingId = hearingId;
        this.courtId = courtId;
        this.courtCentreName = courtCentreName;
        this.hearingTypeId = hearingTypeId;
        this.hearingTypeLabel = hearingTypeLabel;
        this.hearingDates = hearingDates;
        this.hearingDays = hearingDays;
        this.jurisdictionType = jurisdictionType;
        this.judiciaryTypes = judiciaryTypes;
        this.isBoxHearing = isBoxHearing;
        this.courtCentreRoomId = courtCentreRoomId;
        this.courtCentreRoomName = courtCentreRoomName;
        this.courtCentreWelshName = courtCentreWelshName;
        this.courtCentreRoomWelshName = courtCentreRoomWelshName;
        this.courtCentreAddress = courtCentreAddress;
        this.hearingTypeCode = hearingTypeCode;
        this.defendantIds = defendantIds;
        this.courtCentreCode = courtCentreCode;
        this.isVirtualBoxHearing = isVirtualBoxHearing;
        this.estimatedDuration = estimatedDuration;
        this.defenceCounsels = defenceCounsels;

        if (this.hearingDays != null && !hearingDays.isEmpty()) {
            //Overwrite hearingDates as they should be same as hearingDays
            this.hearingDates = hearingDays.stream().map(HearingDayDocument::getSittingDay).map(s -> s.substring(0, 10)).collect(Collectors.toList());
        }
    }

    public String getHearingId() {
        return hearingId;
    }

    public String getCourtId() {
        return courtId;
    }

    public String getCourtCentreName() {
        return courtCentreName;
    }

    public String getHearingTypeId() {
        return hearingTypeId;
    }

    public String getHearingTypeLabel() {
        return hearingTypeLabel;
    }

    public List<String> getHearingDates() {
        return hearingDates;
    }

    public List<HearingDayDocument> getHearingDays() {
        return hearingDays;
    }

    public String getJurisdictionType() {
        return jurisdictionType;
    }

    public List<String> getJudiciaryTypes() {
        return judiciaryTypes;
    }


    public Boolean isIsBoxHearing() {
        return isBoxHearing;
    }

    public Boolean isIsVirtualBoxHearing() {
        return isVirtualBoxHearing;
    }

    public String getCourtCentreRoomId() {
        return courtCentreRoomId;
    }

    public String getCourtCentreRoomName() {
        return courtCentreRoomName;
    }

    public String getCourtCentreWelshName() {
        return courtCentreWelshName;
    }

    public String getCourtCentreRoomWelshName() {
        return courtCentreRoomWelshName;
    }

    public AddressDocument getCourtCentreAddress() {
        return courtCentreAddress;
    }

    public String getHearingTypeCode() {
        return hearingTypeCode;
    }

    public List<String> getDefendantIds() {
        return defendantIds;
    }

    public String getCourtCentreCode() {
        return courtCentreCode;
    }

    public String getEstimatedDuration() {
        return estimatedDuration;
    }

    public List<DefenceCounselDocument> getDefenceCounsels() {
        return defenceCounsels;
    }

    public static final class Builder {

        private String hearingId;
        private String courtId;
        private String courtCentreName;
        private String hearingTypeId;
        private String hearingTypeLabel;
        private List<String> hearingDates;
        private List<HearingDayDocument> hearingDays;
        private String jurisdictionType;
        private List<String> judiciaryTypes;
        private List<String> defendantIds;
        private List<DefenceCounselDocument> defenceCounsels;

        private Boolean isBoxHearing;
        private Boolean isVirtualBoxHearing;

        private String courtCentreRoomId;
        private String courtCentreRoomName;
        private String courtCentreWelshName;
        private String courtCentreRoomWelshName;
        private AddressDocument courtCentreAddress;
        private String hearingTypeCode;
        private String courtCentreCode;
        private String estimatedDuration;

        public Builder withHearingId(final String hearingId) {
            this.hearingId = hearingId;
            return this;
        }

        public Builder withCourtId(final String courtId) {
            this.courtId = courtId;
            return this;
        }

        public Builder withCourtCentreName(final String courtCentreName) {
            this.courtCentreName = courtCentreName;
            return this;
        }

        public Builder withHearingTypeId(final String hearingTypeId) {
            this.hearingTypeId = hearingTypeId;
            return this;
        }

        public Builder withHearingTypLabel(final String hearingTypeLabel) {
            this.hearingTypeLabel = hearingTypeLabel;
            return this;
        }

        public Builder withHearingDays(final List<HearingDayDocument> hearingDays) {
            this.hearingDays = hearingDays;
            return this;
        }

        public Builder withJurisdictionType(final String jurisdictionType) {
            this.jurisdictionType = jurisdictionType;
            return this;
        }

        public Builder withJudiciaryTypes(final List<String> judiciaryTypes) {
            this.judiciaryTypes = judiciaryTypes;
            return this;
        }

        public Builder withBoxHearing(final Boolean boxHearing) {
            isBoxHearing = boxHearing;
            return this;
        }

        public Builder withVirtualBoxHearing(final Boolean virtualBoxHearing) {
            isVirtualBoxHearing = virtualBoxHearing;
            return this;
        }

        public Builder withCourtCentreRoomId(final String courtCentreRoomId) {
            this.courtCentreRoomId = courtCentreRoomId;
            return this;
        }

        public Builder withCourtCentreRoomName(final String courtCentreRoomName) {
            this.courtCentreRoomName = courtCentreRoomName;
            return this;
        }

        public Builder withCourtCentreWelshName(final String courtCentreWelshName) {
            this.courtCentreWelshName = courtCentreWelshName;
            return this;
        }

        public Builder withCourtCentreRoomWelshName(final String courtCentreRoomWelshName) {
            this.courtCentreRoomWelshName = courtCentreRoomWelshName;
            return this;
        }

        public Builder withCourtCentreAddress(final AddressDocument courtCentreAddress) {
            this.courtCentreAddress = courtCentreAddress;
            return this;
        }

        public Builder withHearingTypeCode(final String hearingTypeCode) {
            this.hearingTypeCode = hearingTypeCode;
            return this;
        }

        public Builder withDefendantIds(final List<String> defendantIds) {
            this.defendantIds = defendantIds;
            return this;
        }

        public Builder withCourtCentreCode(final String courtCentreCode) {
            this.courtCentreCode = courtCentreCode;
            return this;
        }

        public Builder withEstimatedDuration(final String estimatedDuration) {
            this.estimatedDuration = estimatedDuration;
            return this;
        }

        public Builder withDefenceCounsels(final List<DefenceCounselDocument> defenceCounsels) {
            this.defenceCounsels = defenceCounsels;
            return this;
        }

        public HearingDocument build() {
            return new HearingDocument(hearingId, courtId, courtCentreName, hearingTypeId, hearingTypeLabel, hearingDates,
                    hearingDays, jurisdictionType, judiciaryTypes, isBoxHearing, courtCentreRoomId, courtCentreRoomName, courtCentreWelshName,
                    courtCentreRoomWelshName, courtCentreAddress, hearingTypeCode, defendantIds, courtCentreCode, isVirtualBoxHearing, estimatedDuration, defenceCounsels);
        }
    }
}
