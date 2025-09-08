package uk.gov.moj.cpp.accesscontrol.drools;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class KieContainerProviderTest {


    @InjectMocks
    private KieContainerProvider kieContainerProvider;

    @Test
    public void shouldGetKieClasspathContainer() throws Exception {

        assertThat(kieContainerProvider.getKieClasspathContainer(), is(notNullValue()));
    }
}