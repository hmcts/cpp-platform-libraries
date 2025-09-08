package uk.gov.justice.services.unifiedsearch.client.domain.cps;

import java.util.List;
import java.util.Objects;

public class CaseDetails {

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
    private List<Party> parties;
    private List<Hearing> hearings;
    private List<LinkedCase> linkedCases;
    private Boolean isLinked;

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(final String caseId) {
        this.caseId = caseId;
    }

    public String getUrn() {
        return urn;
    }

    public void setUrn(final String urn) {
        this.urn = urn;
    }

    public String getCpsUnitCode() {
        return cpsUnitCode;
    }

    public void setCpsUnitCode(final String cpsUnitCode) {
        this.cpsUnitCode = cpsUnitCode;
    }

    public List<String> getCjsAreaCodes() {
        return cjsAreaCodes;
    }

    public void setCjsAreaCodes(final List<String> cjsAreaCodes) {
        this.cjsAreaCodes = cjsAreaCodes;
    }

    public String getCpsAreaCode() {
        return cpsAreaCode;
    }

    public void setCpsAreaCode(final String cpsAreaCode) {
        this.cpsAreaCode = cpsAreaCode;
    }

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(final String caseType) {
        this.caseType = caseType;
    }

    public String getCaseStatusCode() {
        return caseStatusCode;
    }

    public void setCaseStatusCode(final String caseStatusCode) {
        this.caseStatusCode = caseStatusCode;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(final String operationName) {
        this.operationName = operationName;
    }

    public String getParalegalOfficer() {
        return paralegalOfficer;
    }

    public void setParalegalOfficer(final String paralegalOfficer) {
        this.paralegalOfficer = paralegalOfficer;
    }

    public String getCrownAdvocate() {
        return crownAdvocate;
    }

    public void setCrownAdvocate(final String crownAdvocate) {
        this.crownAdvocate = crownAdvocate;
    }

    public String getProsecutor() {
        return prosecutor;
    }

    public void setProsecutor(final String prosecutor) {
        this.prosecutor = prosecutor;
    }

    public String getWitnessCareUnitCode() {
        return witnessCareUnitCode;
    }

    public void setWitnessCareUnitCode(final String witnessCareUnitCode) {
        this.witnessCareUnitCode = witnessCareUnitCode;
    }

    public String getWitnessCareOfficer() {
        return witnessCareOfficer;
    }

    public void setWitnessCareOfficer(final String witnessCareOfficer) {
        this.witnessCareOfficer = witnessCareOfficer;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(final String unit) {
        this.unit = unit;
    }

    public String getUnitGroup() {
        return unitGroup;
    }

    public void setUnitGroup(final String unitGroup) {
        this.unitGroup = unitGroup;
    }

    public List<Party> getParties() {
        return parties;
    }

    public void setParties(final List<Party> parties) {
        this.parties = parties;
    }

    public List<Hearing> getHearings() {
        return hearings;
    }

    public void setHearings(final List<Hearing> hearings) {
        this.hearings = hearings;
    }

    public List<LinkedCase> getLinkedCases() {
        return linkedCases;
    }

    public void setLinkedCases(final List<LinkedCase> linkedCases) {
        this.linkedCases = linkedCases;
    }

    public Boolean getLinked() {
        return isLinked;
    }

    public void setLinked(final Boolean linked) {
        isLinked = linked;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CaseDetails that = (CaseDetails) o;
        return Objects.equals(caseId, that.caseId) &&
                Objects.equals(urn, that.urn) &&
                Objects.equals(cpsUnitCode, that.cpsUnitCode) &&
                Objects.equals(cjsAreaCodes, that.cjsAreaCodes) &&
                Objects.equals(cpsAreaCode, that.cpsAreaCode) &&
                Objects.equals(caseType, that.caseType) &&
                Objects.equals(caseStatusCode, that.caseStatusCode) &&
                Objects.equals(operationName, that.operationName) &&
                Objects.equals(paralegalOfficer, that.paralegalOfficer) &&
                Objects.equals(crownAdvocate, that.crownAdvocate) &&
                Objects.equals(prosecutor, that.prosecutor) &&
                Objects.equals(witnessCareUnitCode, that.witnessCareUnitCode) &&
                Objects.equals(witnessCareOfficer, that.witnessCareOfficer) &&
                Objects.equals(parties, that.parties) &&
                Objects.equals(hearings, that.hearings) &&
                Objects.equals(linkedCases, that.linkedCases) &&
                Objects.equals(isLinked, that.isLinked) &&
                Objects.equals(unit, that.unit) &&
                Objects.equals(unitGroup, that.unitGroup) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(caseId, urn, cpsUnitCode, cjsAreaCodes, cpsAreaCode, caseType, caseStatusCode, operationName, paralegalOfficer, crownAdvocate, prosecutor, witnessCareUnitCode, witnessCareOfficer, unit, unitGroup, parties, hearings, linkedCases, isLinked);
    }
}
