package uk.gov.moj.cpp.accesscontrol.providers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import javax.enterprise.inject.spi.Bean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@link ProviderFoundEvent}
 * class.
 */
@ExtendWith(MockitoExtension.class)
public class ProviderFoundEventTest {

    @Mock
    private Bean<Object> bean;

    private ProviderFoundEvent event;

    @BeforeEach
    public void setup() {
        event = new ProviderFoundEvent(bean);
    }

    @Test
    public void shouldReturnBean() {
        assertThat(event.getBean(), equalTo(bean));
    }
}
