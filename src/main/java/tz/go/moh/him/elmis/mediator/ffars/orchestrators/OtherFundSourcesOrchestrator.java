package tz.go.moh.him.elmis.mediator.ffars.orchestrators;

import akka.actor.ActorSelection;
import org.apache.commons.lang3.tuple.Pair;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the Other Fund Sources orchestrator.
 */
public class OtherFundSourcesOrchestrator extends HealthFundSourcesOrchestrator {

    /**
     * Initializes a new instance of the {@link OtherFundSourcesOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public OtherFundSourcesOrchestrator(MediatorConfig config) {
        super(config);
    }

    /**
     * Handle sending of data to Epicor9
     *
     * @param msg to be sent
     */
    @Override
    protected void sendDataToTargetSystem(String msg) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        String scheme;
        if (config.getProperty("epicor9.secure").equals("true")) {
            scheme = "https";
        } else {
            scheme = "http";
        }

        List<Pair<String, String>> params = new ArrayList<>();

        MediatorHTTPRequest forwardToEpicorRequest = new MediatorHTTPRequest(
                (originalRequest).getRequestHandler(), getSelf(), "Sending Other Fund Sources to Epicor9", "POST", scheme,
                config.getProperty("epicor9.host"), Integer.parseInt(config.getProperty("epicor9.api.port")), config.getProperty("epicor9.api.other_fund_sources.path"),
                msg, headers, params
        );

        ActorSelection httpConnector = getContext().actorSelection(config.userPathFor("http-connector"));
        httpConnector.tell(forwardToEpicorRequest, getSelf());
    }
}
