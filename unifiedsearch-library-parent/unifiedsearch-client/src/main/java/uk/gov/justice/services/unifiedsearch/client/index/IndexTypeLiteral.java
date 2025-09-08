package uk.gov.justice.services.unifiedsearch.client.index;

import javax.enterprise.util.AnnotationLiteral;

public class IndexTypeLiteral extends AnnotationLiteral<IndexType> implements IndexType {

    private static final long serialVersionUID = 1L;
    private final String value;

    public IndexTypeLiteral(String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return this.value;
    }
}
