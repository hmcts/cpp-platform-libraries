package uk.gov.moj.cpp.activiti.spring;

import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.autodeployment.AutoDeploymentStrategy;

/**
 * Custom version of the standard Activiti process engine configuration for Spring.
 *
 * It adds a new auto deployment strategy for correctly identifying duplicate workflow definitions.
 */
public class CustomProcessEngineConfiguration extends SpringProcessEngineConfiguration {

    @Override
    protected AutoDeploymentStrategy getAutoDeploymentStrategy(String mode) {
        if (CustomAutoDeploymentStrategy.DEPLOYMENT_MODE.equals(mode)) {
            return new CustomAutoDeploymentStrategy();
        } else {
            return super.getAutoDeploymentStrategy(mode);
        }
    }
}
