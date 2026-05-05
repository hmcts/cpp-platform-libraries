package uk.gov.moj.cpp.activiti.cdi;

import java.util.Comparator;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import org.activiti.cdi.spi.ProcessEngineLookup;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RuntimeService;

/**
 * CDI producer for Activiti engine services.
 *
 * Bridges the Spring-managed Activiti ProcessEngine to CDI injection points.
 * The ProcessEngine is created by Spring (via ActivitiEngineConfiguration) and
 * made available via the ProxyProcessEngineLookup SPI. This producer exposes
 * the resulting services as CDI application-scoped beans.
 */
@ApplicationScoped
public class ActivitiServicesProducer {

    @Produces
    @ApplicationScoped
    public RuntimeService runtimeService() {
        return processEngineLookup().getProcessEngine().getRuntimeService();
    }

    @Produces
    @ApplicationScoped
    public HistoryService historyService() {
        return processEngineLookup().getProcessEngine().getHistoryService();
    }

    @Produces
    @ApplicationScoped
    public ManagementService managementService() {
        return processEngineLookup().getProcessEngine().getManagementService();
    }

    private ProcessEngineLookup processEngineLookup() {
        final ServiceLoader<ProcessEngineLookup> loader = ServiceLoader.load(
                ProcessEngineLookup.class, Thread.currentThread().getContextClassLoader());
        return StreamSupport.stream(loader.spliterator(), false)
                .max(Comparator.comparingInt(ProcessEngineLookup::getPrecedence))
                .orElseThrow(() -> new IllegalStateException("No ProcessEngineLookup implementation found on classpath"));
    }
}
