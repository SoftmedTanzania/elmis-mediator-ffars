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
import tz.go.moh.him.elmis.mediator.ffars.domain.FundSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Represents unit tests for the {@link OtherFundSourcesOrchestrator} class.
 */
public class OtherFundSourcesOrchestratorTest extends BaseOrchestratorTest {

    /**
     * Runs initialization before test execution.
     */
    @Before
    public void before() throws Exception {
        super.before();

        List<MockLauncher.ActorToLaunch> toLaunch = new LinkedList<>();
        toLaunch.add(new MockLauncher.ActorToLaunch("http-connector", MockEpicor9.class));
        TestingUtils.launchActors(system, configuration.getName(), toLaunch);
    }

    /**
     * Performs other fund source HTTP request test
     */
    @Test
    public void testOtherFundSourceHTTPRequest() throws Exception {
        assertNotNull(configuration);

        new JavaTestKit(system) {{
            final ActorRef orchestrator = system.actorOf(Props.create(OtherFundSourcesOrchestrator.class, configuration));

            InputStream stream = OtherFundSourcesOrchestratorTest.class.getClassLoader().getResourceAsStream("fund_source_request.json");

            assertNotNull(stream);

            MediatorHTTPRequest request = new MediatorHTTPRequest(
                    getRef(),
                    getRef(),
                    "unit-test",
                    "POST",
                    "http",
                    null,
                    null,
                    configuration.getProperty("epicor9.api.other_fund_sources.path"),
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
     * Represents a mock class for the Epicor9 system.
     */
    private static class MockEpicor9 extends MockHTTPConnector {
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
            InputStream stream = OtherFundSourcesOrchestrator.class.getClassLoader().getResourceAsStream("fund_source_request.json");

            assertNotNull(stream);

            Gson gson = new Gson();

            FundSource expected;

            try {
                expected = gson.fromJson(IOUtils.toString(stream), FundSource.class);
            } catch (IOException e) {
                // TODO: handle this issue
                return;
            }

            FundSource actual = gson.fromJson(msg.getBody(), FundSource.class);

            assertEquals(expected.getName(), actual.getName());
            assertEquals(expected.getCode(), actual.getCode());
            assertEquals(expected.getDescription(), actual.getDescription());
            assertEquals(expected.getDisplayOrder(), actual.getDisplayOrder());
            assertEquals(expected.getLastUpdate(), actual.getLastUpdate());
        }
    }
}
