package uk.gov.moj.cpp.activiti.spring;

import org.activiti.spring.autodeployment.DefaultAutoDeploymentStrategy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.VfsResource;

/**
 * Custom version of the default auto deployment strategy.
 *
 * Adds support for VFS resources, as used by Wildfly WAR deployments. Uses the filename of the
 * resource (not the path), to correctly identify duplicate workflow definitions from different
 * deployments of the same WAR.
 */
public class CustomAutoDeploymentStrategy extends DefaultAutoDeploymentStrategy {

    /**
     * The deployment mode this strategy handles.
     */
    public static final String DEPLOYMENT_MODE = "custom";

    @Override
    protected String getDeploymentMode() {
        return DEPLOYMENT_MODE;
    }

    @Override
    protected String determineResourceName(final Resource resource) {
        if (resource instanceof VfsResource) {
            return resource.getFilename();
        } else {
            return super.determineResourceName(resource);
        }
    }
}
