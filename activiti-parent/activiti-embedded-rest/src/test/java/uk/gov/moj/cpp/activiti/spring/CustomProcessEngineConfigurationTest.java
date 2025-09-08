package uk.gov.moj.cpp.activiti.spring;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

import org.activiti.spring.autodeployment.DefaultAutoDeploymentStrategy;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link CustomProcessEngineConfiguration} class.
 */
public class CustomProcessEngineConfigurationTest {

    private CustomProcessEngineConfiguration configuration = new CustomProcessEngineConfiguration();

    @Test
    public void shouldReturnCustomAUtoDeploymentStrategy() {
        assertThat(configuration.getAutoDeploymentStrategy(CustomAutoDeploymentStrategy.DEPLOYMENT_MODE), instanceOf(CustomAutoDeploymentStrategy.class));
    }

    @Test
    public void shouldReturnDefaultDeploymentStrategy() {
        assertThat(configuration.getAutoDeploymentStrategy(DefaultAutoDeploymentStrategy.DEPLOYMENT_MODE), instanceOf(DefaultAutoDeploymentStrategy.class));
    }
}
