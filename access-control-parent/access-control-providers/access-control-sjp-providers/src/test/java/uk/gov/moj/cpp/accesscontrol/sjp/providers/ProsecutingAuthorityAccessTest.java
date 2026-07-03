package uk.gov.moj.cpp.accesscontrol.sjp.providers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class ProsecutingAuthorityAccessTest {

    private static final String PROSECUTOR = "PROC1";

    @Test
    public void shouldBuildNoProsecutingAuthorityAccess() {

        ProsecutingAuthorityAccess prosecutingAuthorityAccess = ProsecutingAuthorityAccess.NONE;

        assertThat(prosecutingAuthorityAccess.getProsecutingAuthority(), nullValue());
        assertThat(prosecutingAuthorityAccess.hasAccess(PROSECUTOR), is(false));
        assertThat(prosecutingAuthorityAccess.hasAccess(null), is(false));
        assertThat(prosecutingAuthorityAccess.hasAccess(""), is(false));
    }

    @Test
    public void shouldBuildSingleProsecutingAuthorityAccess() {

        ProsecutingAuthorityAccess prosecutingAuthorityAccess = ProsecutingAuthorityAccess.of(PROSECUTOR);

        assertThat(prosecutingAuthorityAccess.getProsecutingAuthority(), is(PROSECUTOR));
        assertThat(prosecutingAuthorityAccess.hasAccess(PROSECUTOR), is(true));
        assertThat(prosecutingAuthorityAccess.hasAccess(null), is(false));
        assertThat(prosecutingAuthorityAccess.hasAccess(""), is(false));
    }

    @Test
    public void shouldReturnFalseForInvalidProsecutingAuthorityAccess() {

        ProsecutingAuthorityAccess prosecutingAuthorityAccess = ProsecutingAuthorityAccess.of("OTHER_PROSECUTOR");

        assertThat(prosecutingAuthorityAccess.getProsecutingAuthority(), is("OTHER_PROSECUTOR"));
        assertThat(prosecutingAuthorityAccess.hasAccess(PROSECUTOR), is(false));
        assertThat(prosecutingAuthorityAccess.hasAccess(null), is(false));
        assertThat(prosecutingAuthorityAccess.hasAccess(""), is(false));
    }

    @Test
    public void shouldGrantAccessViaAgentProsecutorAuthorityAccess() {

        final ProsecutingAuthorityAccess prosecutingAuthorityAccess =
                ProsecutingAuthorityAccess.of(PROSECUTOR, List.of("AGENT1", "AGENT2"));

        assertThat(prosecutingAuthorityAccess.getProsecutingAuthority(), is(PROSECUTOR));
        assertThat(prosecutingAuthorityAccess.getAgentProsecutorAuthorityAccess(), is(List.of("AGENT1", "AGENT2")));
        assertThat(prosecutingAuthorityAccess.hasAccess(PROSECUTOR), is(true));
        assertThat(prosecutingAuthorityAccess.hasAccess("AGENT1"), is(true));
        assertThat(prosecutingAuthorityAccess.hasAccess("AGENT2"), is(true));
        assertThat(prosecutingAuthorityAccess.hasAccess("OTHER"), is(false));
    }

    @Test
    public void shouldBuildAllProsecutingAuthorityAccess() {

        ProsecutingAuthorityAccess prosecutingAuthorityAccess = ProsecutingAuthorityAccess.ALL;

        assertThat(prosecutingAuthorityAccess.getProsecutingAuthority(), is("ALL"));
        assertThat(prosecutingAuthorityAccess.hasAccess(PROSECUTOR), is(true));
        assertThat(prosecutingAuthorityAccess.hasAccess(null), is(true));
        assertThat(prosecutingAuthorityAccess.hasAccess(""), is(true));
    }
}
