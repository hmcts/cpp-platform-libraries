package uk.gov.justice.services.unifiedsearch.client.domain;

import java.util.List;
import java.util.Objects;

public class DefenceCounsel {

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

    public void setId(final String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(final String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public List<String> getDefendants() {
        return defendants;
    }

    public void setDefendants(final List<String> defendants) {
        this.defendants = defendants;
    }

    public List<String> getAttendanceDays() {
        return attendanceDays;
    }

    public void setAttendanceDays(final List<String> attendanceDays) {
        this.attendanceDays = attendanceDays;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final DefenceCounsel that = (DefenceCounsel) o;
        return id.equals(that.id) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "DefenceCounsel{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                '}';
    }
}
