package tz.go.moh.him.elmis.mediator.ffars.orchestrators;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openhim.mediator.engine.messages.FinishRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.testing.MockHTTPConnector;
import org.openhim.mediator.engine.testing.MockLauncher;
import org.openhim.mediator.engine.testing.TestingUtils;
import tz.go.moh.him.elmis.mediator.ffars.domain.ActualBalance;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Represents unit tests for the {@link ActualBalanceOrchestrator} class.
 */
public class ActualBalanceOrchestratorTest extends BaseOrchestratorTest {

    /**
     * Runs initialization before test execution.
     */
    @Before
    public void before() throws Exception {
        super.before();

        List<MockLauncher.ActorToLaunch> toLaunch = new LinkedList<>();
        toLaunch.add(new MockLauncher.ActorToLaunch("http-connector", MockElmis.class));
        TestingUtils.launchActors(system, configuration.getName(), toLaunch);
    }

    /**
     * Performs actual balance HTTP request test
     */
    @Test
    public void testActualBalanceHTTPRequest() throws Exception {
        assertNotNull(configuration);

        new JavaTestKit(system) {{
            final ActorRef orchestrator = system.actorOf(Props.create(ActualBalanceOrchestrator.class, configuration));

            InputStream stream = ActualBalanceOrchestratorTest.class.getClassLoader().getResourceAsStream("actual_balance_request.json");

            assertNotNull(stream);

            MediatorHTTPRequest request = new MediatorHTTPRequest(
                    getRef(),
                    getRef(),
                    "unit-test",
                    "POST",
                    "http",
                    null,
                    null,
                    configuration.getProperty("elmis.actual_balance.path"),
                    IOUtils.toString(stream),
                    Collections.singletonMap("Content-Type", "application/json"),
                    Collections.emptyList()
            );

            orchestrator.tell(request, getRef());

            final Object[] out = new ReceiveWhile<Object>(Object.class, duration("3 seconds")) {
                @Override
                protected Object match(Object msg) {
                    if (msg instanceof FinishRequest) {
                        return msg;
                    }
                    throw noMatch();
                }
            }.get();

            Assert.assertTrue(Arrays.stream(out).anyMatch(c -> c instanceof FinishRequest));
        }};
    }

    /**
     * Represents a mock class for the eLMIS system.
     */
    private static class MockElmis extends MockHTTPConnector {
        /**
         * Gets the response.
         *
         * @return Returns the response.
         */
        @Override
        public String getResponse() {
            return null;
        }

        /**
         * Gets the status code.
         *
         * @return Returns the status code.
         */
        @Override
        public Integer getStatus() {
            return 200;
        }

        /**
         * Gets the HTTP headers.
         *
         * @return Returns the HTTP headers.
         */
        @Override
        public Map<String, String> getHeaders() {
            return Collections.emptyMap();
        }

        /**
         * Handles the message.
         *
         * @param msg The message.
         */
        @Override
        public void executeOnReceive(MediatorHTTPRequest msg) {
            InputStream stream = ActualBalanceOrchestrator.class.getClassLoader().getResourceAsStream("actual_balance_request.json");

            assertNotNull(stream);

            Gson gson = new Gson();

            ActualBalance expected;

            try {
                expected = gson.fromJson(IOUtils.toString(stream), ActualBalance.class);
            } catch (IOException e) {
                // TODO: handle this issue
                return;
            }

            ActualBalance actual = gson.fromJson(msg.getBody(), ActualBalance.class);

            assertEquals(expected.getUid(), actual.getUid());
            assertEquals(expected.getFacilityCode(), actual.getFacilityCode());
            assertEquals(expected.getPeriodStartDate(), actual.getPeriodStartDate());
            assertEquals(expected.getSourceApplication(), actual.getSourceApplication());

            assertEquals(expected.getLineItems().size(), actual.getLineItems().size());

            assertEquals(expected.getLineItems().get(0).getAllocatedBudget(), actual.getLineItems().get(0).getAllocatedBudget());
            assertEquals(expected.getLineItems().get(0).isAdditive(), actual.getLineItems().get(0).isAdditive());
            assertEquals(expected.getLineItems().get(0).getFundSourceCode(), actual.getLineItems().get(0).getFundSourceCode());
            assertEquals(expected.getLineItems().get(0).getNotes(), actual.getLineItems().get(0).getNotes());

            assertEquals(expected.getLineItems().get(1).getAllocatedBudget(), actual.getLineItems().get(1).getAllocatedBudget());
            assertEquals(expected.getLineItems().get(1).isAdditive(), actual.getLineItems().get(1).isAdditive());
            assertEquals(expected.getLineItems().get(1).getFundSourceCode(), actual.getLineItems().get(1).getFundSourceCode());
            assertEquals(expected.getLineItems().get(1).getNotes(), actual.getLineItems().get(1).getNotes());
        }
    }
}
