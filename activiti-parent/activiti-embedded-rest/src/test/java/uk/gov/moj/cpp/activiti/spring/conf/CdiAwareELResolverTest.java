package uk.gov.moj.cpp.activiti.spring.conf;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;

import org.activiti.engine.impl.javax.el.ELContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CdiAwareELResolverTest {

    private CdiAwareELResolver resolver;

    @BeforeEach
    public void createResolver() {
        resolver = new CdiAwareELResolver();
    }

    @Test
    public void shouldResolveCdiBeanByNameWhenBaseIsNull() {
        final Object expectedBean = new Object();
        final ELContext context = mock(ELContext.class);

        try (MockedStatic<CDI> mockedCdi = mockStatic(CDI.class)) {
            final CDI<Object> cdiInstance = mock(CDI.class);
            final Instance<Object> instance = mock(Instance.class);

            mockedCdi.when(CDI::current).thenReturn(cdiInstance);
            when(cdiInstance.select(eq(Object.class), any())).thenReturn(instance);
            when(instance.get()).thenReturn(expectedBean);

            final Object result = resolver.getValue(context, null, "uploadFile");

            assertThat(result, is(expectedBean));
            verify(context).setPropertyResolved(true);
        }
    }

    @Test
    public void shouldReturnNullWhenBaseIsNotNull() {
        final ELContext context = mock(ELContext.class);
        final Object base = new Object();

        final Object result = resolver.getValue(context, base, "uploadFile");

        assertThat(result, is(nullValue()));
        verify(context, never()).setPropertyResolved(true);
    }

    @Test
    public void shouldReturnNullWhenPropertyIsNull() {
        final ELContext context = mock(ELContext.class);

        final Object result = resolver.getValue(context, null, null);

        assertThat(result, is(nullValue()));
        verify(context, never()).setPropertyResolved(true);
    }

    @Test
    public void shouldReturnNullWhenCdiLookupFails() {
        final ELContext context = mock(ELContext.class);

        try (MockedStatic<CDI> mockedCdi = mockStatic(CDI.class)) {
            mockedCdi.when(CDI::current).thenThrow(new IllegalStateException("No CDI context"));

            final Object result = resolver.getValue(context, null, "unknownBean");

            assertThat(result, is(nullValue()));
            verify(context, never()).setPropertyResolved(true);
        }
    }

    @Test
    public void shouldBeReadOnly() {
        final ELContext context = mock(ELContext.class);

        assertThat(resolver.isReadOnly(context, null, "anyBean"), is(true));
    }

    @Test
    public void shouldReturnObjectClassAsCommonPropertyType() {
        final ELContext context = mock(ELContext.class);

        assertThat(resolver.getCommonPropertyType(context, null), is(Object.class));
    }

    @Test
    public void shouldReturnNullForType() {
        final ELContext context = mock(ELContext.class);

        assertThat(resolver.getType(context, null, "anyBean"), is(nullValue()));
    }

    @Test
    public void shouldReturnNullForFeatureDescriptors() {
        final ELContext context = mock(ELContext.class);

        assertThat(resolver.getFeatureDescriptors(context, null), is(nullValue()));
    }
}
