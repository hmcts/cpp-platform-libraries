package uk.gov.moj.cpp.activiti.cdi;

import static java.lang.String.format;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.activiti.cdi.spi.ProcessEngineLookup;
import org.activiti.engine.ProcessEngine;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Process engine lookup class that returns a proxy process engine.
 *
 * This class is used by CDI to look up the process engine, and is configured for use by CDI using
 * the service definition in META-INF/services. The proxy engine created is actually a proxy object
 * that allows the actual creation to be delayed.
 *
 * This is also a Spring component so that the real process engine can be retrieved from the Spring
 * context once it has been created.
 */
@Component
public class ProxyProcessEngineLookup implements ProcessEngineLookup, ApplicationContextAware {

    private static final ProcessEngineInvocationHandler HANDLER = new ProcessEngineInvocationHandler();

    private static ProcessEngine proxy;

    public int getPrecedence() {
        return 20;
    }

    @Override
    public ProcessEngine getProcessEngine() {
        if (proxy == null) {
            synchronized (ProxyProcessEngineLookup.class) {
                if (proxy == null) {
                    proxy = (ProcessEngine) Proxy.newProxyInstance(
                            ProcessEngine.class.getClassLoader(),
                            new Class<?>[] { ProcessEngine.class },
                            HANDLER);
                }
            }
        }
        return proxy;
    }

    @Override
    public void ungetProcessEngine() {
        if (proxy != null) {
            synchronized (ProxyProcessEngineLookup.class) {
                if (proxy != null) {
                    proxy.close();
                    proxy = null;
                }
            }
        }
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        HANDLER.setProcessEngine(applicationContext.getBean("processEngine", ProcessEngine.class));
    }

    /**
     * Invocation handler to wrap a {@link ProcessEngine} in a proxy so that creation of the underlying
     * engine can be delayed until the Spring context is ready.
     */
    private static class ProcessEngineInvocationHandler implements InvocationHandler {

        private ProcessEngine processEngine;

        public void setProcessEngine(ProcessEngine processEngine) {
            this.processEngine = processEngine;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (processEngine != null) {
                return method.invoke(processEngine, args);
            } else {
                throw new IllegalStateException(format(
                        "Could not invoke method %s because process engine has not been initialised", method.getName()));
            }
        }
    }
}
