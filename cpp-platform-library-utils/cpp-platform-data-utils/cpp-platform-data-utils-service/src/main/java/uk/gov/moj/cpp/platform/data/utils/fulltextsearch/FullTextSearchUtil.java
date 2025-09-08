package uk.gov.moj.cpp.platform.data.utils.fulltextsearch;

public class FullTextSearchUtil {
    private static final String WILD_CARD = ":*";
    private static final String WILD_CARD_AND = ":* & ";

    public static String getSearchTerm(final String userQuery) {

        final String userQueryTmp = userQuery.trim().replaceAll("[^\\w.-]+", " ");
        final String[] tmp = userQueryTmp.split(" ");
        final StringBuilder strReplaced = new StringBuilder();

        for (int i = 0; i < tmp.length; i++) {
            if (i == tmp.length - 1) {
                strReplaced.append(tmp[i].concat(WILD_CARD));
            } else {
                strReplaced.append(tmp[i].concat(WILD_CARD_AND));
            }
        }

        return strReplaced.toString();
    }
}
