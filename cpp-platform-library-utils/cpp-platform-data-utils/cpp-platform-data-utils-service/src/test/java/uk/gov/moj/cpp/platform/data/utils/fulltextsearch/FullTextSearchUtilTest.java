package uk.gov.moj.cpp.platform.data.utils.fulltextsearch;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FullTextSearchUtilTest {

    private FullTextSearchUtil searchUtil = new FullTextSearchUtil();

    @Test
    public void shouldProcessSearchTerm() {
        assertThat(searchUtil.getSearchTerm("love"), is("love:*"));
    }

    @Test
    public void shouldProcessSearchTerm2() {
        assertThat(searchUtil.getSearchTerm("list of search terms"), is("list:* & of:* & search:* & terms:*"));
    }

    @Test
    public void shouldProcessSearchTermWithSpecialCharacter() {
        assertThat(searchUtil.getSearchTerm("list%%^$%$of&*^*&search terms"), is("list:* & of:* & search:* & terms:*"));
    }
}
