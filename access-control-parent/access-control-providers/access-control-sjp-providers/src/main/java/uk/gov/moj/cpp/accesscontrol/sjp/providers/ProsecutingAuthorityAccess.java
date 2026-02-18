package uk.gov.moj.cpp.accesscontrol.sjp.providers;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class ProsecutingAuthorityAccess {

    public static final String ALL_PROSECUTING_AUTHORITIES = "ALL";

    public final static ProsecutingAuthorityAccess NONE = new ProsecutingAuthorityAccess(null);
    public final static ProsecutingAuthorityAccess ALL = new ProsecutingAuthorityAccess(ALL_PROSECUTING_AUTHORITIES);

    private String prosecutingAuthority;

    private List<String> agentProsecutorAuthorityAccess;

    private ProsecutingAuthorityAccess(final String prosecutingAuthority) {
        this.prosecutingAuthority = prosecutingAuthority;
    }

    private ProsecutingAuthorityAccess(final String prosecutingAuthority, final List<String> agentProsecutorAuthorityAccess) {
        this.prosecutingAuthority = prosecutingAuthority;
        this.agentProsecutorAuthorityAccess = agentProsecutorAuthorityAccess;
    }

    public static ProsecutingAuthorityAccess of(final String prosecutingAuthority, final List<String> agentProsecutorAuthorityAccess) {
        if (StringUtils.isEmpty(prosecutingAuthority)) {
            NONE.agentProsecutorAuthorityAccess = agentProsecutorAuthorityAccess;
            return NONE;
        } else if (prosecutingAuthority.equals(ALL.getProsecutingAuthority())) {
            ALL.agentProsecutorAuthorityAccess = agentProsecutorAuthorityAccess;
            return ALL;
        }

        return new ProsecutingAuthorityAccess(prosecutingAuthority, agentProsecutorAuthorityAccess);
    }

    public String getProsecutingAuthority() {
        return prosecutingAuthority;
    }

    public boolean hasAccess(final String prosecutingAuthority) {
        return ALL.getProsecutingAuthority().equals(this.getProsecutingAuthority()) ||
                (this.getProsecutingAuthority() != null &&
                        this.getProsecutingAuthority().equals(prosecutingAuthority)) ||
                (agentProsecutorAuthorityAccess != null && agentProsecutorAuthorityAccess.contains(prosecutingAuthority));
    }
}
