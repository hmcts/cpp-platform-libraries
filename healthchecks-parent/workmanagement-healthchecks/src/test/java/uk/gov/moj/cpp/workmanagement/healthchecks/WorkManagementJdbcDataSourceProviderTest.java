package uk.gov.moj.cpp.workmanagement.healthchecks;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.moj.cpp.workmanagement.healthchecks.WorkManagementJdbcDataSourceProvider.DATA_SOURCE_NAME;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.services.jdbc.persistence.JdbcRepositoryException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class WorkManagementJdbcDataSourceProviderTest {

    @Mock
    private InitialContext initialContext;

    @InjectMocks
    private WorkManagementJdbcDataSourceProvider workManagementJdbcDataSourceProvider;

    @Test
    public void shouldGetCorrectNamedDataSource() throws Exception {

        final DataSource dataSource = mock(DataSource.class);
        when(initialContext.lookup(DATA_SOURCE_NAME)).thenReturn(dataSource);

        assertThat(workManagementJdbcDataSourceProvider.getDataSource(), is(dataSource));
    }

    @Test
    public void shouldFailIfGettingDataSourceFails() throws Exception {

        final NamingException namingException = new NamingException("Ooops");

        when(initialContext.lookup(DATA_SOURCE_NAME)).thenThrow(namingException);

        try {
            workManagementJdbcDataSourceProvider.getDataSource();
            fail();
        } catch (final JdbcRepositoryException expected) {
            assertThat(expected.getMessage(), is("Failed to lookup workmanagement DataSource using JNDI name 'java:/DS.workmanagement'"));
            assertThat(expected.getCause(), is(namingException));
        }
    }
}