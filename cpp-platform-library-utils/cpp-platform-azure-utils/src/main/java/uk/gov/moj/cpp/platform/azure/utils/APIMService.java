package uk.gov.moj.cpp.platform.azure.utils;

import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class APIMService {

    private static final Logger LOGGER = LoggerFactory.getLogger(APIMService.class);

    @Inject
    private RestEasyClientService restEasyClientService;


    public Response triggerAPIMServiceWithRetryOnFailures(final String requestPayload, final int[] retryMinutes, final int retryCount, final long seconds, final String apimUrl, final String subscriptionKey) throws InterruptedException {

        int retry = 0;
        int status = 0;
        Response response;

        do {
            if (status >= 500) {
                LOGGER.error("Issue connecting to APIM service; retrying after {} Mins... ", retryMinutes[retry]);
                final long timeOut = retryMinutes[retry] * seconds;
                TimeUnit.SECONDS.sleep(timeOut);
                retry++;
            }

            response = restEasyClientService.post(apimUrl, requestPayload, subscriptionKey);

            status = response.getStatus();
            LOGGER.info("Azure Function {} invoked with Request: {} Received response status: {}",
                    apimUrl, requestPayload, status);

        } while (retry < retryCount && status >= 500);

        return response;
    }
}
