package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document;

public class AddressDocument {

    private String address1;
    private String address2;
    private String address3;
    private String address4;
    private String address5;
    private String postCode;

    public AddressDocument(final String address1, final String address2, final String address3, final String address4, final String address5, final String postCode) {
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
        this.address4 = address4;
        this.address5 = address5;
        this.postCode = postCode;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(final String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(final String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(final String address3) {
        this.address3 = address3;
    }

    public String getAddress4() {
        return address4;
    }

    public void setAddress4(final String address4) {
        this.address4 = address4;
    }

    public String getAddress5() {
        return address5;
    }

    public void setAddress5(final String address5) {
        this.address5 = address5;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(final String postCode) {
        this.postCode = postCode;
    }


    public static class Builder {

        private String address1;
        private String address2;
        private String address3;
        private String address4;
        private String address5;
        private String postCode;

        public Builder setAddress1(final String address1) {
            this.address1 = address1;
            return this;
        }

        public Builder setAddress2(final String address2) {
            this.address2 = address2;
            return this;
        }

        public Builder setAddress3(final String address3) {
            this.address3 = address3;
            return this;
        }

        public Builder setAddress4(final String address4) {
            this.address4 = address4;
            return this;
        }

        public Builder setAddress5(final String address5) {
            this.address5 = address5;
            return this;
        }

        public Builder setPostCode(final String postCode) {
            this.postCode = postCode;
            return this;
        }

        public AddressDocument build() {
            return new AddressDocument(address1, address2, address3, address4, address5, postCode);
        }
    }

}
