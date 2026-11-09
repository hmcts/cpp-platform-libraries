package uk.gov.moj.cpp.activiti.rest;

import static java.util.stream.Collectors.toSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

import uk.gov.moj.cpp.activiti.rest.resource.HistoricProcessInstancesResource;
import uk.gov.moj.cpp.activiti.rest.resource.JobsResource;
import uk.gov.moj.cpp.activiti.rest.resource.ProcessInstancesResource;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Stream;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.Path;

import org.junit.jupiter.api.Test;

class ActivitiRestApplicationTest {

    private final ActivitiRestApplication application = new ActivitiRestApplication();

    @Test
    void shouldBeDeployedUnderTheActivitiServicePath() {
        assertThat(ActivitiRestApplication.class.getAnnotation(ApplicationPath.class).value(), is("/internal/activiti/service"));
    }

    @Test
    void shouldPublishOnlyTheActivitiResources() {
        assertThat(application.getClasses(), containsInAnyOrder(
                ProcessInstancesResource.class,
                HistoricProcessInstancesResource.class,
                JobsResource.class));
    }

    @Test
    void shouldPublishEveryRootResourceInTheResourcePackage() throws Exception {
        assertThat(application.getClasses(), is(rootResourcesInThePackageOf(ProcessInstancesResource.class)));
    }

    private static Set<Class<?>> rootResourcesInThePackageOf(final Class<?> type) throws Exception {
        final String packageName = type.getPackageName();
        final java.nio.file.Path classesDirectory = Paths.get(type.getResource(type.getSimpleName() + ".class").toURI()).getParent();
        try (Stream<java.nio.file.Path> files = Files.list(classesDirectory)) {
            return files
                    .map(file -> file.getFileName().toString())
                    .filter(name -> name.endsWith(".class") && !name.contains("$"))
                    .map(name -> load(packageName + "." + name.substring(0, name.length() - ".class".length())))
                    .filter(candidate -> candidate.isAnnotationPresent(Path.class))
                    .collect(toSet());
        }
    }

    private static Class<?> load(final String className) {
        try {
            return Class.forName(className);
        } catch (final ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}
