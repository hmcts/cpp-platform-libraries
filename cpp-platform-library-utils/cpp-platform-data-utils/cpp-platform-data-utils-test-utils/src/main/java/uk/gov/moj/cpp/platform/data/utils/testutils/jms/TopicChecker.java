package uk.gov.moj.cpp.platform.data.utils.testutils.jms;


import org.awaitility.Durations;
import uk.gov.justice.services.test.utils.common.host.TestHostProvider;
import uk.gov.justice.services.test.utils.core.rest.RestClient;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonReader;
import javax.ws.rs.core.Response;
import java.io.StringReader;
import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.justice.services.messaging.JsonObjects.jsonReaderFactory;

public class TopicChecker {
    private static final int SUBSCRIPTION_NAME_INDEX = 2;
    private static final int SUBSCRIPTION_MESSAGE_COUNT_INDEX = 4;
    private static final int EMPTY_SUBSCRIPTION_COUNT = 0;

    private TopicChecker() {

    }

    public static void waitUntilQueueIsEmpty(final ContextSubscriptionName contextSubscriptionName, final String topicName) {
        await()
                .pollDelay(Duration.ZERO)
                .pollInterval(Durations.TWO_HUNDRED_MILLISECONDS)
                .until(() -> {
                            final JsonArray topicSubscriptions = getSubscriptionsFor(topicName);
                            for (int i = 0; i < topicSubscriptions.size(); i++) {
                                final JsonArray subscription = topicSubscriptions.getJsonArray(i);
                                if (isSubscriptionAvailable(contextSubscriptionName, subscription) && isEmpty(subscription)) {
                                    return true;
                                }
                            }

                            return false;
                        }
                );
    }

    /**
     * Do a REST call to the Jolokia endpoint
     *
     * @return list of all available subscriptions
     */
    private static JsonArray getSubscriptionsFor(final String topicName) {
        final String url = "http://" + TestHostProvider.getHost() + ":8161/jolokia/exec/org.apache.activemq.artemis:type=Broker,brokerName=%22default%22,module=JMS,serviceType=Topic,name=%22" + topicName + "%22/listAllSubscriptions()";
        final Response response = new RestClient().query(url, "text/plain");
        assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));

        final String bodyAsString = response.readEntity(String.class);
        try (final JsonReader jsonReader = jsonReaderFactory.createReader(new StringReader(bodyAsString))) {
            return jsonReader.readObject().getJsonArray("value");
        }
    }


    /*
     *  Raml file in event listener have registered subscription name for that listener for eg : baseUri: message://event/listener/message/miReportDataAssignment
     *  So in the above example "miReportDataAssignment" is the subscriptionName for assginment listener.
     *  Below method retrive all the available subcriptions for that listener and iterate to find out whether provider listener available or not.
     * */
    private static boolean isSubscriptionAvailable(final ContextSubscriptionName contextSubscriptionName, final JsonArray subscription) {
        return subscription.get(SUBSCRIPTION_NAME_INDEX).toString().startsWith(contextSubscriptionName.getSubscriptionName());

    }

    /*
     *  Once the above method isSubscriptionAvailable() finds the subscription name then this method
     *  will check whether the message is consumed by the registered listener or not.
     * */
    private static boolean isEmpty(final JsonArray subscription) {
        return ((JsonNumber) subscription.get(SUBSCRIPTION_MESSAGE_COUNT_INDEX)).intValue() == EMPTY_SUBSCRIPTION_COUNT;
    }


}
