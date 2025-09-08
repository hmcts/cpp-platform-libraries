package uk.gov.moj.cpp.accesscontrol.providers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.HashSet;

import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProviderAnnotationScannerTest {

    private static final String TEST_EVENT_NAME = "Test-Event";

    @Mock
    private AfterDeploymentValidation afterDeploymentValidation;

    @Mock
    private BeanManager beanManager;

    @Mock
    private Bean<Object> bean;

    @Captor
    private ArgumentCaptor<ProviderFoundEvent> captor;

    private ProviderAnnotationScanner annotationScanner;

    @BeforeEach
    public void setup() {
        annotationScanner = new ProviderAnnotationScanner();
    }

    @Test
    public void shouldFireProviderFoundEvent() throws Exception {
        doReturn(TestProvider.class).when(bean).getBeanClass();

        mockBeanManagerGetBeansWith(bean);

        annotationScanner.afterDeploymentValidation(afterDeploymentValidation, beanManager);

        verify(beanManager).fireEvent(captor.capture());
        assertThat(captor.getValue(), instanceOf(ProviderFoundEvent.class));
        assertThat(captor.getValue().getBean(), equalTo(bean));
    }

    @Test
    public void shouldNotFireAnyEventForNonProviderEvents() throws Exception {
        doReturn(Object.class).when(bean).getBeanClass();

        mockBeanManagerGetBeansWith(bean);

        annotationScanner.afterDeploymentValidation(afterDeploymentValidation, beanManager);

        verify(beanManager, never()).fireEvent(any());
    }

    @SuppressWarnings("serial")
    private void mockBeanManagerGetBeansWith(Bean<Object> handler) {
        doReturn(new HashSet<Bean<Object>>() {
            {
                add(handler);
            }
        }).when(beanManager).getBeans(any(), any());
    }

    @Provider
    public static class TestProvider {

    }
}
