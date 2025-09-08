package uk.gov.moj.cpp.accesscontrol.progression.providers;


import java.util.function.Function;

public enum ProsecutingAuthority {

    CPS;

    public static Function<String, Boolean> isCPSProsecutedCase
            = prosecutingAuthority -> CPS.name().equalsIgnoreCase(prosecutingAuthority);
}
