package uk.gov.moj.cpp.platform.data.utils.persistence.factory;

import static com.google.common.collect.Sets.newTreeSet;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import javax.enterprise.inject.Instance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DataQueryFactoryTest {

    @Mock
    private Instance<CustomDataQueryProvider> dataQueryProviderProviderInstance;

    @InjectMocks
    private DataQueryFactory dataQueryFactory;

    @BeforeEach
    public void setUp() {
        when(dataQueryProviderProviderInstance.isUnsatisfied()).thenReturn(true);
    }

    @Test
    public void shouldBuildQuery() {
        dataQueryFactory.initCustomQueryProvider();
        final String query = dataQueryFactory.buildQuery("table1", emptySet());
        assertThat(query, is("select * from table1 where last_modified_ts between ? and ?"));
    }

    @Test
    public void shouldBuildQueryWithIncludedColumns() {
        dataQueryFactory.initCustomQueryProvider();
        final String query = dataQueryFactory.buildQuery("table1", newTreeSet(asList("column3", "column2", "column1")));
        assertThat(query, is("select column1,column2,column3 from table1 where last_modified_ts between ? and ?"));
    }

    @Test
    public void shouldBuildQueryUsingCustomQueryProvider() {
        when(dataQueryProviderProviderInstance.isAmbiguous()).thenReturn(false);
        when(dataQueryProviderProviderInstance.isUnsatisfied()).thenReturn(false);
        final CustomDataQueryProvider mockedCustomDataQueryProvider = mock(CustomDataQueryProvider.class);
        when(dataQueryProviderProviderInstance.get()).thenReturn(mockedCustomDataQueryProvider);
        when(mockedCustomDataQueryProvider.getCustomQuery("tableName")).thenReturn(Optional.of("some-query"));
        dataQueryFactory.initCustomQueryProvider();

        final String query = dataQueryFactory.buildQuery("tableName", emptySet());
        assertThat(query, is("some-query"));
    }
}