package uk.gov.moj.cpp.workmanagement.healthchecks;

import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.moj.cpp.workmanagement.healthchecks.CamundaDatabaseHealthcheck.TABLE_NAMES;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.services.healthcheck.api.HealthcheckResult;
import uk.gov.justice.services.healthcheck.utils.database.TableChecker;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

@ExtendWith(MockitoExtension.class)
public class CamundaDatabaseHealthcheckTest {
  @Mock
  private WorkManagementJdbcDataSourceProvider workManagementJdbcDataSourceProvider;

  @Mock
  private TableChecker tableChecker;

  @Mock
  private Logger logger;

  @InjectMocks
  private CamundaDatabaseHealthcheck camundaDatabaseHealthcheck;

  @Test
  public void shouldReturnCorrectHealthcheckName() throws Exception {

    assertThat(camundaDatabaseHealthcheck.getHealthcheckName(), is("work-management-camunda-database-healthcheck"));
  }

  @Test
  public void shouldReturnCorrectHealthcheckDescription() throws Exception {

    assertThat(camundaDatabaseHealthcheck.healthcheckDescription(), is("Checks connectivity to the workmanagement (camunda) database and that all tables are available"));
  }

  @Test
  public void shouldGetListOfExpectedTablesFromViewStoreAsHealthcheck() throws Exception {

    final DataSource viewStoreDataSource = mock(DataSource.class);
    final HealthcheckResult healthcheckResult = mock(HealthcheckResult.class);

    when(workManagementJdbcDataSourceProvider.getDataSource()).thenReturn(viewStoreDataSource);
    when(tableChecker.checkTables(TABLE_NAMES, viewStoreDataSource)).thenReturn(healthcheckResult);

    assertThat(camundaDatabaseHealthcheck.runHealthcheck(), is(healthcheckResult));
  }

  @Test
  public void shouldReturnHealthcheckFailureIfAccessingTheViewStoreThrowsSqlException() throws Exception {

    final SQLException sqlException = new SQLException("Oops");
    final DataSource viewStoreDataSource = mock(DataSource.class);

    when(workManagementJdbcDataSourceProvider.getDataSource()).thenReturn(viewStoreDataSource);
    when(tableChecker.checkTables(TABLE_NAMES, viewStoreDataSource)).thenThrow(sqlException);

    final HealthcheckResult healthcheckResult = camundaDatabaseHealthcheck.runHealthcheck();

    assertThat(healthcheckResult.isPassed(), is(false));
    assertThat(healthcheckResult.getErrorMessage().isPresent(), is(true));
    assertThat(healthcheckResult.getErrorMessage(), is(of("Exception thrown accessing workmanagement database. java.sql.SQLException: Oops")));

    verify(logger).error("Healthcheck for workmanagement database failed.", sqlException);
  }
}