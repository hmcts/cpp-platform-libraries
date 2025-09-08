package uk.gov.moj.cpp.accesscontrol.drools.util;

public class ProviderNameUtil {

    /**
     * Extracts the {@link Provider}'s name from the className. For consistency providers are loaded
     * into the drools session and referenced via a consistent naming strategy. Which is the
     * classname but with a lower-case starting letter.
     *
     * @param className - the name of the provider class to convert.
     * @return string name of the provider class as to be used in drools session.
     */
    public static String getProviderName(final String className) {
        final StringBuilder sb = new StringBuilder();
        sb.append(className.substring(0, 1).toLowerCase());
        sb.append(className.substring(1, className.length()));
        return sb.toString();
    }

}
