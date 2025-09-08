package uk.gov.moj.cpp.activiti.spring;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.jboss.vfs.VFS;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.VfsResource;

/**
 * Unit tests for the {@link CustomAutoDeploymentStrategy} class.
 */
public class CustomAutoDeploymentStrategyTest {

    private CustomAutoDeploymentStrategy strategy = new CustomAutoDeploymentStrategy();

    @Test
    public void shouldHaveCustomDeploymentMode() {
        assertThat(strategy.getDeploymentMode(), equalTo("custom"));
    }

    @Test
    public void shouldUseFilenameAsNameForVfsResources() {
        final VfsResource resource = new VfsResource(VFS.getChild("/my/random/path/test-name"));
        assertThat(strategy.determineResourceName(resource), equalTo("test-name"));
    }
}
