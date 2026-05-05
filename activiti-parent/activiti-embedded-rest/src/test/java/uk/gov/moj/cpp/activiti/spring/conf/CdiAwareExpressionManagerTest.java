package uk.gov.moj.cpp.activiti.spring.conf;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;

import org.activiti.engine.delegate.VariableScope;
import org.activiti.engine.impl.el.ActivitiElContext;
import org.activiti.engine.impl.javax.el.CompositeELResolver;
import org.activiti.engine.impl.javax.el.ELContext;
import org.activiti.engine.impl.javax.el.ELResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CdiAwareExpressionManagerTest {

    private CdiAwareExpressionManager manager;

    @BeforeEach
    public void createManager() {
        manager = new CdiAwareExpressionManager();
    }

    @Test
    public void shouldReturnNonNullResolverFromCreateElResolver() {
        final VariableScope variableScope = mock(VariableScope.class);

        final ELResolver resolver = manager.createElResolver(variableScope);

        assertThat(resolver, is(notNullValue()));
    }

    @Test
    public void shouldReturnCompositeELResolver() {
        final VariableScope variableScope = mock(VariableScope.class);

        final ELResolver resolver = manager.createElResolver(variableScope);

        assertThat(resolver, is(instanceOf(CompositeELResolver.class)));
    }

    @Test
    public void shouldResolveCdiBeanViaCompositeResolver() {
        final VariableScope variableScope = mock(VariableScope.class);
        final Object expectedBean = new Object();
        // ActivitiElContext has concrete isPropertyResolved() tracking needed by CompositeELResolver
        final ELContext context = new ActivitiElContext(mock(ELResolver.class));

        try (MockedStatic<CDI> mockedCdi = mockStatic(CDI.class)) {
            final CDI<Object> cdiInstance = mock(CDI.class);
            @SuppressWarnings("unchecked")
            final Instance<Object> instance = mock(Instance.class);

            mockedCdi.when(CDI::current).thenReturn(cdiInstance);
            when(cdiInstance.select(eq(Object.class), any())).thenReturn(instance);
            when(instance.get()).thenReturn(expectedBean);

            final ELResolver resolver = manager.createElResolver(variableScope);
            final Object result = resolver.getValue(context, null, "uploadFile");

            assertThat(result, is(expectedBean));
        }
    }
}
