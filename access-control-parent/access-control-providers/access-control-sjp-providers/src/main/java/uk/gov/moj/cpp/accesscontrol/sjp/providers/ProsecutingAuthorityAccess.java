package uk.gov.moj.cpp.accesscontrol.sjp.providers;

import org.apache.commons.lang3.StringUtils;

public class ProsecutingAuthorityAccess {

    public static final String ALL_PROSECUTING_AUTHORITIES = "ALL";

    public final static ProsecutingAuthorityAccess NONE = new ProsecutingAuthorityAccess(null);
    public final static ProsecutingAuthorityAccess ALL = new ProsecutingAuthorityAccess(ALL_PROSECUTING_AUTHORITIES);

    private String prosecutingAuthority;

    private ProsecutingAuthorityAccess(final String prosecutingAuthority) {
        this.prosecutingAuthority = prosecutingAuthority;
    }

    public static ProsecutingAuthorityAccess of(final String prosecutingAuthority) {

        if (StringUtils.isEmpty(prosecutingAuthority)) {
            return NONE;
        } else if (prosecutingAuthority.equals(ALL.getProsecutingAuthority())) {
            return ALL;
        }

        return new ProsecutingAuthorityAccess(prosecutingAuthority);
    }

    public String getProsecutingAuthority() {
        return prosecutingAuthority;
    }

    public boolean hasAccess(final String prosecutingAuthority) {
        return ALL.getProsecutingAuthority().equals(this.getProsecutingAuthority()) ||
                (this.getProsecutingAuthority() != null &&
                        this.getProsecutingAuthority().equals(prosecutingAuthority));
    }
}
