package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document;

import java.util.List;

public class DefenceCounselDocument {

    private String id;
    private String title;
    private String firstName;
    private String middleName;
    private String lastName;
    private String status;
    private List<String> defendants;
    private List<String> attendanceDays;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getDefendants() {
        return defendants;
    }

    public List<String> getAttendanceDays() {
        return attendanceDays;
    }

    public DefenceCounselDocument(final String id, final String title, final String firstName, final String middleName, final String lastName, final String status, final List<String> defendants, final List<String> attendanceDays) {
        this.id = id;
        this.title = title;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.status = status;
        this.defendants = defendants;
        this.attendanceDays = attendanceDays;
    }

    public static class Builder {
        private String id;
        private String title;
        private String firstName;
        private String middleName;
        private String lastName;
        private String status;
        private List<String> defendants;
        private List<String> attendanceDays;

        public Builder withId(final String id) {
            this.id = id;
            return this;
        }

        public Builder withTitle(final String title) {
            this.title = title;
            return this;
        }

        public Builder withFirstName(final String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder withMiddleName(final String middleName) {
            this.middleName = middleName;
            return this;
        }

        public Builder withLastName(final String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder withStatus(final String status) {
            this.status = status;
            return this;
        }

        public Builder withDefendants(final List<String> defendants) {
            this.defendants = defendants;
            return this;
        }

        public Builder withAttendanceDays(final List<String> attendanceDays) {
            this.attendanceDays = attendanceDays;
            return this;
        }

        public DefenceCounselDocument build(){
            return new DefenceCounselDocument(id, title, firstName,middleName,lastName,status,defendants,attendanceDays);
        }
    }
}
