package uk.gov.justice.services.unifiedsearch.client.domain;

import java.util.Objects;

public class Alias {

    private String title;
    private String firstName;
    private String middleName;
    private String lastName;
    private String organisationName;

    public String getTitle() {
        return title;
    }

    public Alias setTitle(final String title) {
        this.title = title;
        return this;
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

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(final String organisationName) {
        this.organisationName = organisationName;
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alias alias = (Alias) o;
        return Objects.equals(title, alias.title) &&
                Objects.equals(firstName, alias.firstName) &&
                Objects.equals(middleName, alias.middleName) &&
                Objects.equals(lastName, alias.lastName) &&
                Objects.equals(organisationName, alias.organisationName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, firstName, middleName, lastName, organisationName);
    }
}
