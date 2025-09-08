package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.cps;

import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.BaseCaseDocument;

import java.util.List;

public class CaseDocument implements BaseCaseDocument {

    private final String caseId;
    private final String urn;
    private final String cpsUnitCode;
    private final List<String> cjsAreaCodes;
    private final String cpsAreaCode;
    private final String caseType;
    private final String caseStatusCode;
    private final String operationName;
    private final String paralegalOfficer;
    private final String crownAdvocate;
    private final String prosecutor;
    private final String witnessCareUnitCode;
    private final String witnessCareOfficer;
    private final String unit;
    private final String unitGroup;
    private final List<PartyDocument> parties;
    private final List<HearingDocument> hearings;
    private final List<LinkedCaseDocument> linkedCases;

    public CaseDocument(final String caseId, final String urn, final String cpsUnitCode, final List<String> cjsAreaCodes, final String cpsAreaCode, final String caseType, final String caseStatusCode, final String operationName, final String paralegalOfficer, final String crownAdvocate, final String prosecutor, final String witnessCareUnitCode, final String witnessCareOfficer, final String unit, final String unitGroup, final List<PartyDocument> parties, final List<HearingDocument> hearings, final List<LinkedCaseDocument> linkedCases) {
        this.caseId = caseId;
        this.urn = urn;
        this.cpsUnitCode = cpsUnitCode;
        this.cjsAreaCodes = cjsAreaCodes;
        this.cpsAreaCode = cpsAreaCode;
        this.caseType = caseType;
        this.caseStatusCode = caseStatusCode;
        this.operationName = operationName;
        this.paralegalOfficer = paralegalOfficer;
        this.crownAdvocate = crownAdvocate;
        this.prosecutor = prosecutor;
        this.witnessCareUnitCode = witnessCareUnitCode;
        this.witnessCareOfficer = witnessCareOfficer;
        this.unit = unit;
        this.unitGroup = unitGroup;
        this.parties = parties;
        this.hearings = hearings;
        this.linkedCases = linkedCases;
    }

    public String getCaseId() {
        return caseId;
    }

    public String getUrn() {
        return urn;
    }

    public String getCpsUnitCode() {
        return cpsUnitCode;
    }

    public List<String> getCjsAreaCodes() {
        return cjsAreaCodes;
    }

    public String getCpsAreaCode() {
        return cpsAreaCode;
    }

    public String getCaseType() {
        return caseType;
    }

    public String getCaseStatusCode() {
        return caseStatusCode;
    }

    public String getOperationName() {
        return operationName;
    }

    public String getParalegalOfficer() {
        return paralegalOfficer;
    }

    public String getCrownAdvocate() {
        return crownAdvocate;
    }

    public String getProsecutor() {
        return prosecutor;
    }

    public String getWitnessCareUnitCode() {
        return witnessCareUnitCode;
    }

    public String getWitnessCareOfficer() {
        return witnessCareOfficer;
    }

    public String getUnit() {
        return unit;
    }

    public String getUnitGroup() {
        return unitGroup;
    }

    public List<PartyDocument> getParties() {
        return parties;
    }

    public List<HearingDocument> getHearings() {
        return hearings;
    }

    public List<LinkedCaseDocument> getLinkedCases() {
        return linkedCases;
    }

    public static class Builder {

        private String caseId;
        private String urn;
        private String cpsUnitCode;
        private List<String> cjsAreaCodes;
        private String cpsAreaCode;
        private String caseType;
        private String caseStatusCode;
        private String operationName;
        private String paralegalOfficer;
        private String crownAdvocate;
        private String prosecutor;
        private String witnessCareUnitCode;
        private String witnessCareOfficer;
        private String unit;
        private String unitGroup;
        private List<PartyDocument> parties;
        private List<HearingDocument> hearings;
        private List<LinkedCaseDocument> linkedCases;

        public Builder withCaseId(final String caseId) {
            this.caseId = caseId;
            return this;
        }

        public Builder withUrn(final String urn) {
            this.urn = urn;
            return this;
        }

        public Builder withCpsUnitCode(final String cpsUnitCode) {
            this.cpsUnitCode = cpsUnitCode;
            return this;
        }

        public Builder withCjsAreaCode(final List<String> cjsAreaCode) {
            this.cjsAreaCodes = cjsAreaCode;
            return this;
        }

        public Builder withCpsAreaCode(final String cpsAreaCode) {
            this.cpsAreaCode = cpsAreaCode;
            return this;
        }

        public Builder withCaseType(final String caseType) {
            this.caseType = caseType;
            return this;
        }

        public Builder withCaseStatusCode(final String caseStatusCode) {
            this.caseStatusCode = caseStatusCode;
            return this;
        }

        public Builder withOperationName(final String operationName) {
            this.operationName = operationName;
            return this;
        }

        public Builder withParalegalOfficer(final String paralegalOfficer) {
            this.paralegalOfficer = paralegalOfficer;
            return this;
        }

        public Builder withCrownAdvocate(final String crownAdvocate) {
            this.crownAdvocate = crownAdvocate;
            return this;
        }
        public Builder withProsecutor(final String prosecutor) {
            this.prosecutor = prosecutor;
            return this;
        }

        public Builder withWitnessCareUnitCode(final String witnessCareUnitCode) {
            this.witnessCareUnitCode = witnessCareUnitCode;
            return this;
        }
        public Builder withWitnessCareOfficer(final String witnessCareOfficer) {
            this.witnessCareOfficer = witnessCareOfficer;
            return this;
        }

        public Builder withUnit(final String unit) {
            this.unit = unit;
            return this;
        }
        public Builder withUnitGroup(final String unitGroup) {
            this.unitGroup = unitGroup;
            return this;
        }

        public Builder withParties(final List<PartyDocument> parties) {
            this.parties = parties;
            return this;
        }

        public Builder withHearings(final List<HearingDocument> hearings) {
            this.hearings = hearings;
            return this;
        }

        public Builder withLinkedCases(final List<LinkedCaseDocument> linkedCases) {
            this.linkedCases = linkedCases;
            return this;
        }

        public CaseDocument build() {
            return new CaseDocument(caseId, urn, cpsUnitCode, cjsAreaCodes, cpsAreaCode, caseType, caseStatusCode,
                    operationName, paralegalOfficer, crownAdvocate, prosecutor, witnessCareUnitCode, witnessCareOfficer,
                    unit, unitGroup, parties, hearings, linkedCases);
        }


    }

}
