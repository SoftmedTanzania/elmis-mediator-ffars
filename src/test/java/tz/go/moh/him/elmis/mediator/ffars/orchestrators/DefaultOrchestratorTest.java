package tz.go.moh.him.elmis.mediator.ffars.orchestrators;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openhim.mediator.engine.messages.FinishRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertNotNull;

/**
 * Contains tests for the {@link DefaultOrchestrator} class.
 */
public class DefaultOrchestratorTest extends BaseOrchestratorTest {

    /**
     * Performs health fund source HTTP request test
     */
    @Test
    public void testFundSourcesInboundHTTPRequest() throws Exception {
        assertNotNull(configuration);

        new JavaTestKit(system) {{
            final ActorRef orchestrator = system.actorOf(Props.create(DefaultOrchestrator.class, configuration));

            InputStream stream = DefaultOrchestratorTest.class.getClassLoader().getResourceAsStream("fund_source_request.json");

            assertNotNull(stream);

            MediatorHTTPRequest request = new MediatorHTTPRequest(
                    getRef(),
                    getRef(),
                    "unit-test",
                    "POST",
                    "http",
                    null,
                    null,
                    "/fund-sources-inbound",
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
     * Performs an invalid health fund source HTTP request test
     */
    @Test
    public void testInvalidFundSourcesInboundHTTPRequest() throws Exception {
        assertNotNull(configuration);

        new JavaTestKit(system) {{
            final ActorRef orchestrator = system.actorOf(Props.create(DefaultOrchestrator.class, configuration));

            InputStream stream = DefaultOrchestratorTest.class.getClassLoader().getResourceAsStream("fund_source_invalid_request.json");

            assertNotNull(stream);

            MediatorHTTPRequest request = new MediatorHTTPRequest(
                    getRef(),
                    getRef(),
                    "unit-test",
                    "POST",
                    "http",
                    null,
                    null,
                    "/fund-sources-inbound",
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
            Assert.assertTrue(Arrays.stream(out).allMatch(c -> (c instanceof FinishRequest) && ((FinishRequest) c).getResponse().equals("Bad Request")));
        }};
    }
}