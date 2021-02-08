package tz.go.moh.him.elmis.mediator.ffars.orchestrators;

import akka.actor.ActorSelection;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
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

        String host;
        int port;
        String path;
        String scheme;

        if (config.getDynamicConfig().isEmpty()) {
            log.debug("Dynamic config is empty, using config from mediator.properties");

            host = config.getProperty("epicor9.host");
            port = Integer.parseInt(config.getProperty("epicor9.port"));
            path = config.getProperty("epicor9.other_fund_sources.path");

            if (config.getProperty("epicor9.secure").equals("true")) {
                scheme = "https";
            } else {
                scheme = "http";
            }
        } else {
            log.debug("Using dynamic config");

            JSONObject connectionProperties = new JSONObject(config.getDynamicConfig()).getJSONObject("epicor9ConnectionProperties");

            host = connectionProperties.getString("epicor9Host");
            port = connectionProperties.getInt("epicor9Port");
            path = connectionProperties.getString("epicor9FundSourcesPath");
            scheme = connectionProperties.getString("epicor9Scheme");

            String username = connectionProperties.getString("epicor9Username");
            String password = connectionProperties.getString("epicor9Password");

            // if we have a username and a password
            // we want to add the username and password as the Basic Auth header in the HTTP request
            if (username != null && !"".equals(username) && password != null && !"".equals(password))
            {
                headers.put("Authorization", "Basic " + Base64.encodeBase64URLSafeString((username + ":" + password).getBytes()));
            }
        }

        List<Pair<String, String>> params = new ArrayList<>();

        MediatorHTTPRequest forwardToEpicorRequest = new MediatorHTTPRequest(
                (originalRequest).getRequestHandler(), getSelf(), "Sending Other Fund Sources to Epicor9", "POST", scheme, host, port, path, msg, headers, params
        );

        ActorSelection httpConnector = getContext().actorSelection(config.userPathFor("http-connector"));
        httpConnector.tell(forwardToEpicorRequest, getSelf());
    }
}
