package uk.gov.justice.services.unifiedsearch.client.domain;

import java.util.List;

public class CaseDetails {
    private String caseId;
    private String caseReference;
    private String prosecutingAuthority;
    private String caseStatus;
    private String _case_type;
    private Boolean _is_sjp;
    private Boolean _is_magistrates;
    private Boolean _is_crown;
    private Boolean _is_charging;
    private String sjpNoticeServed;
    private String sourceSystemReference;
    private List<Party> parties;
    private List<Hearing> hearings;
    private List<Application> applications;

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(final String caseId) {
        this.caseId = caseId;
    }

    public String getCaseReference() {
        return caseReference;
    }

    public void setCaseReference(final String caseReference) {
        this.caseReference = caseReference;
    }

    public String getProsecutingAuthority() {
        return prosecutingAuthority;
    }

    public void setProsecutingAuthority(final String prosecutingAuthority) {
        this.prosecutingAuthority = prosecutingAuthority;
    }

    public String getCaseStatus() {
        return caseStatus;
    }

    public void setCaseStatus(final String caseStatus) {
        this.caseStatus = caseStatus;
    }

    public String get_case_type() {
        return _case_type;
    }

    public void set_case_type(final String _case_type) {
        this._case_type = _case_type;
    }

    public Boolean get_is_sjp() {
        return _is_sjp;
    }

    public void set_is_sjp(final Boolean _is_sjp) {
        this._is_sjp = _is_sjp;
    }

    public Boolean get_is_magistrates() {
        return _is_magistrates;
    }

    public void set_is_magistrates(final Boolean _is_magistrates) {
        this._is_magistrates = _is_magistrates;
    }

    public Boolean get_is_crown() {
        return _is_crown;
    }

    public void set_is_crown(final Boolean _is_crown) {
        this._is_crown = _is_crown;
    }

    public Boolean get_is_charging() {
        return _is_charging;
    }

    public void set_is_charging(final Boolean _is_charging) {
        this._is_charging = _is_charging;
    }

    public List<Hearing> getHearings() {
        return hearings;
    }

    public void setHearings(final List<Hearing> hearings) {
        this.hearings = hearings;
    }

    public List<Party> getParties() {
        return parties;
    }

    public void setParties(final List<Party> parties) {
        this.parties = parties;
    }

    public List<Application> getApplications() {
        return applications;
    }

    public void setApplications(final List<Application> applications) {
        this.applications = applications;
    }

    public String getSjpNoticeServed() {
        return sjpNoticeServed;
    }

    public void setSjpNoticeServed(final String sjpNoticeServed) {
        this.sjpNoticeServed = sjpNoticeServed;
    }

    public String getSourceSystemReference() {
        return sourceSystemReference;
    }

    public void setSourceSystemReference(final String sourceSystemReference) {
        this.sourceSystemReference = sourceSystemReference;
    }
}
