package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document;

import static java.util.stream.Collectors.toList;

import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.BaseCaseDocument;

import java.util.List;

public class CaseDocument implements BaseCaseDocument {

    private final String caseId;
    private final String caseReference;
    private final String prosecutingAuthority;
    private final String caseStatus;
    private final String _case_type;
    private final boolean _is_sjp;
    private final boolean _is_magistrates;
    private final boolean _is_crown;
    private final boolean _is_charging;
    private final String sjpNoticeServed;
    private final String sourceSystemReference;
    private final List<PartyDocument> parties;
    private final List<HearingDocument> hearings;
    private final List<ApplicationDocument> applications;


    public CaseDocument(final String caseId, final String caseReference, final String prosecutingAuthority, final String caseStatus, final String _case_type,
                        final boolean _is_sjp, final boolean _is_magistrates, final boolean _is_crown, final boolean _is_charging,
                        final String sjpNoticeServed, final List<PartyDocument> parties, final List<HearingDocument> hearings, final List<ApplicationDocument> applications, final String sourceSystemReference) {

        this.caseId = caseId;
        this.caseReference = caseReference;
        this.prosecutingAuthority = prosecutingAuthority;
        this.caseStatus = caseStatus;
        this._case_type = _case_type;
        this._is_sjp = _is_sjp;
        this._is_magistrates = _is_magistrates;
        this._is_crown = _is_crown;
        this._is_charging = _is_charging;
        this.sjpNoticeServed = sjpNoticeServed;
        this.parties = parties;
        this.hearings = hearings;
        this.applications = applications;
        this.sourceSystemReference = sourceSystemReference;
    }


    public String getCaseId() {
        return caseId;
    }

    public String getCaseReference() {
        return caseReference;
    }


    public String getProsecutingAuthority() {
        return prosecutingAuthority;
    }

    public String getCaseStatus() {
        return caseStatus;
    }

    public String get_case_type() {
        return _case_type;
    }

    public boolean is_is_sjp() {
        return _is_sjp;
    }

    public boolean is_is_magistrates() {
        return _is_magistrates;
    }

    public boolean is_is_crown() {
        return _is_crown;
    }

    public boolean is_is_charging() {
        return _is_charging;
    }

    public String getSjpNoticeServed() {
        return sjpNoticeServed;
    }

    public List<PartyDocument> getParties() {
        return parties;
    }

    public List<HearingDocument> getHearings() {
        return hearings;
    }

    public List<ApplicationDocument> getApplications() {
        return applications;
    }

    public String getSourceSystemReference() {
        return sourceSystemReference;
    }

    public static class Builder {

        private String caseId;
        private String caseReference;
        private String prosecutingAuthority;
        private String caseStatus;
        private String _case_type;
        private boolean _is_sjp;
        private boolean _is_magistrates;
        private boolean _is_crown;
        private boolean _is_charging;
        private String sjpNoticeServed;
        private List<PartyDocument.Builder> parties;
        private List<HearingDocument.Builder> hearings;
        private List<ApplicationDocument.Builder> applications;
        private String sourceSystemReference;

        public Builder withCaseId(final String caseId) {
            this.caseId = caseId;
            return this;
        }

        public Builder withCaseReference(final String caseReference) {
            this.caseReference = caseReference;
            return this;
        }

        public Builder withProsecutingAuthority(final String prosecutingAuthority) {
            this.prosecutingAuthority = prosecutingAuthority;
            return this;
        }

        public Builder withCaseStatus(final String caseStatus) {
            this.caseStatus = caseStatus;
            return this;
        }

        public Builder with_case_type(final String _case_type) {
            this._case_type = _case_type;
            return this;
        }

        public Builder with_is_sjp(final boolean _is_sjp) {
            this._is_sjp = _is_sjp;
            return this;
        }

        public Builder with_is_magistrates(final boolean _is_magistrates) {
            this._is_magistrates = _is_magistrates;
            return this;
        }

        public Builder with_is_crown(final boolean _is_crown) {
            this._is_crown = _is_crown;
            return this;
        }

        public Builder with_is_charging(final boolean _is_charging) {
            this._is_charging = _is_charging;
            return this;
        }

        public Builder withSjpNoticeServed(final String sjpNoticeServed) {
            this.sjpNoticeServed = sjpNoticeServed;
            return this;
        }

        public Builder withParties(final List<PartyDocument.Builder> parties) {
            this.parties = parties;
            return this;
        }

        public Builder withHearings(final List<HearingDocument.Builder> hearings) {
            this.hearings = hearings;
            return this;
        }

        public Builder withApplications(final List<ApplicationDocument.Builder> applications) {
            this.applications = applications;
            return this;
        }

        public List<PartyDocument.Builder> getPartyBuilders() {
            return parties;
        }

        public List<HearingDocument.Builder> getHearingBuilders() {
            return hearings;
        }

        public List<ApplicationDocument.Builder> getApplicationBuilders() {
            return applications;
        }

        public Builder withSourceSystemReference(final String sourceSystemReference) {
            this.sourceSystemReference = sourceSystemReference;
            return this;
        }

        public CaseDocument build() {
            return new CaseDocument(caseId, caseReference, prosecutingAuthority, caseStatus, _case_type,
                    _is_sjp, _is_magistrates, _is_crown, _is_charging,
                    sjpNoticeServed,
                    parties.stream().map(PartyDocument.Builder::build).collect(toList()),
                    hearings.stream().map(HearingDocument.Builder::build).collect(toList()),
                    applications.stream().map(ApplicationDocument.Builder::build).collect(toList()),
                    sourceSystemReference);
        }


    }
}
