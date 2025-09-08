package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.refdata;

import static java.lang.Math.abs;
import static java.lang.Math.random;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationTypes {


    private static final int APPLICATION_LIST_SIZE = 16;

    private static final List<ApplicationType> applicationTypes =
            asList(
                    new ApplicationType("BA76504", "Application to vary conditions of bail"),
                    new ApplicationType("BA76505", "Application to review bail decision"),
                    new ApplicationType("BA76509", "Application to vary bail conditions imposed by police"),
                    new ApplicationType("BA76510", "Application for bail pending appeal"),
                    new ApplicationType("BA76511", "Application for bail following committal/sending/transfer in custody to appear at Crown Court"),
                    new ApplicationType("CJ01501", "Application closure order for unlicensed premises"),
                    new ApplicationType("CJ01502", "Application to discharge a closure order - unlicensed premises"),
                    new ApplicationType("CJ03506", "Failing to comply with the community requirements of a suspended sentence order"),
                    new ApplicationType("CJ03507", "Commission of a further offence during the operational period of a suspended sentence order"),
                    new ApplicationType("CJ03508", "Application to amend a suspended sentence order on change of residence"),
                    new ApplicationType("CJ03509", "Application to amend the requirements of a suspended sentence order"),
                    new ApplicationType("CJ03510", "Failing to comply with the requirements of a community order"),
                    new ApplicationType("CJ03511", "Conviction of an offence while a community order is in force"),
                    new ApplicationType("CJ03512", "Application to amend a community order on change of residence"),
                    new ApplicationType("CJ03513", "Application to amend the requirements of a community order"),
                    new ApplicationType("CJ03519", "Application to revoke a community order without re-sentencing")
            );

    private static final Map<String, ApplicationType> applicationTypeMap = new HashMap<>();
    private static final List<String> applicationTypeCodes = new ArrayList<>();

    static {
        applicationTypes.forEach(at -> {
            applicationTypeMap.put(at.getApplicationCode(), at);
            applicationTypeCodes.add(at.getApplicationCode());
        });
    }


    public static List<ApplicationType> randomApplicationTypes(final int count) {
        final List<ApplicationType> applicationTypes = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            applicationTypes.add(randomApplicationType());
        }

        return applicationTypes;
    }

    public static ApplicationType randomApplicationType() {
        final String applicationCode = applicationTypeCodes.get(randomIndex());
        return applicationTypeMap.get(applicationCode);
    }

    public static ApplicationType applicationType(final String applicationTypeCode) {
        return applicationTypeMap.get(applicationTypeCode);
    }


    private static int randomIndex() {
        int index = (int) abs(random() * 10) + (int) abs(random() * 10);
        while (index >= APPLICATION_LIST_SIZE) {
            index = (int) abs(random() * 10) + (int) abs(random() * 10);
        }

        return index;

    }
}
