package uk.gov.moj.cpp.systemidmapper.client;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.justice.services.common.converter.jackson.ObjectMapperProducer;
import uk.gov.justice.services.common.http.DefaultServerPortProvider;

import javax.inject.Inject;

import org.apache.openejb.jee.Application;
import org.apache.openejb.jee.WebApp;
import org.apache.openejb.junit.ApplicationComposer;
import org.apache.openejb.testing.Classes;
import org.apache.openejb.testing.Module;

@RunWith(ApplicationComposer.class)
public class SystemIdMapperClientProducerInjectionTest {

    @Inject
    SystemIdMapperClient systemIdMapperClient;

    @Module
    @Classes(cdi = true, value = {
            SystemIdMapperClientProducer.class,
            DefaultServerPortProvider.class,
            ObjectMapperProducer.class
    })
    public WebApp war() {
        return new WebApp()
                .contextRoot("test-mapper-producer")
                .addServlet("TestApp", Application.class.getName());
    }

    @Test
    public void shouldContainInstanceOfSystemIdMapperClient() throws Exception {
        assertThat(systemIdMapperClient, notNullValue());
    }
}
