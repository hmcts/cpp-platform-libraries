package uk.gov.justice.services.unifiedsearch.client.transformer;

import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.justice.services.unifiedsearch.client.transformer.CaseDetailsJsonHelper.defendantCounsels;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import uk.gov.justice.services.unifiedsearch.client.domain.DefenceCounsel;


public class DefenceCounselHelper {

    public static void assertDefenceCounselWithinHearing(final DefenceCounsel defenceCounsel, final int  defenceCounselIndex) throws IOException {
        final DefenceCounsel expectedDefenceCounsel = new ObjectMapper().readValue(defendantCounsels.get(defenceCounselIndex).getJsonObject(0).toString(), DefenceCounsel.class);

        assertThat(defenceCounsel, is(expectedDefenceCounsel));
    }
}
