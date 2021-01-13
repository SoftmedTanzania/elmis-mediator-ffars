package tz.go.moh.him.elmis.mediator.ffars.orchestrators;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpStatus;
import org.codehaus.plexus.util.StringUtils;
import org.json.JSONObject;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.FinishRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPResponse;
import tz.go.moh.him.elmis.mediator.ffars.domain.FundSource;
import tz.go.moh.him.mediator.core.domain.ErrorMessage;
import tz.go.moh.him.mediator.core.domain.ResultDetail;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Represents the Health Fund Sources orchestrator.
 */
public class HealthFundSourcesOrchestrator extends UntypedActor {
    /**
     * The logger instance.
     */
    protected final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    /**
     * The mediator configuration.
     */
    protected final MediatorConfig config;

    /**
     * Represents the original mediator request.
     */
    protected MediatorHTTPRequest originalRequest;

    /**
     * Represents an Error Messages Definition Resource Object defined in <a href="file:../resources/error-messages.json">/resources/error-messages.json</a>.
     */
    protected JSONObject errorMessageResource;

    /**
     * Represents a list of error messages, if any,that have been caught during payload data validation to be returned to the source system as response.
     */
    protected List<ErrorMessage> errorMessages = new ArrayList<>();

    /**
     * Initializes a new instance of the {@link HealthFundSourcesOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public HealthFundSourcesOrchestrator(MediatorConfig config) {
        this.config = config;

        InputStream stream = getClass().getClassLoader().getResourceAsStream("error-messages.json");
        try {
            if (stream != null) {
                errorMessageResource = new JSONObject(IOUtils.toString(stream)).getJSONObject("HEALTH_FUND_SOURCES_ERROR_MESSAGES");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles data validations
     *
     * @param fundSource The object to be validated
     */
    protected void validateData(FundSource fundSource) {
        List<ResultDetail> resultDetailsList = new ArrayList<>();

        if (fundSource == null) {
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ERROR_INVALID_PAYLOAD"), null));
        } else {
            resultDetailsList.addAll(validateRequiredFields(fundSource));
        }

        if (resultDetailsList.size() != 0) {
            // Adding the validation results to the Error message object
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setSource(new Gson().toJson(fundSource));
            errorMessage.setResultsDetails(resultDetailsList);
            errorMessages.add(errorMessage);
        }
    }

    /**
     * Validate Fund Sources Required Fields
     *
     * @param fundSource to be validated
     * @return array list of validation results details for failed validations
     */
    public List<ResultDetail> validateRequiredFields(FundSource fundSource) {
        List<ResultDetail> resultDetailsList = new ArrayList<>();

        if (StringUtils.isBlank(fundSource.getName()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("NAME_IS_BLANK"), null));

        if (StringUtils.isBlank(fundSource.getCode()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("CODE_IS_BLANK"), null));

        if (StringUtils.isBlank(fundSource.getDisplayOrder()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("DISPLAY_ORDER_IS_BLANK"), null));

        if (StringUtils.isBlank(fundSource.getLastUpdate()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("LAST_UPDATE_IS_BLANK"), null));

        return resultDetailsList;
    }

    /**
     * Handles receiving OpenHIM Core messages into the mediator
     * @param msg to be received
     *
     * @throws Exception
     */
    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof MediatorHTTPRequest) {
            originalRequest = (MediatorHTTPRequest) msg;

            log.info("Received request: " + originalRequest.getHost() + " " + originalRequest.getMethod() + " " + originalRequest.getPath());

            FundSource fundSource = null;
            try {
                Type domainType = new TypeToken<FundSource>() {}.getType();
                fundSource = new Gson().fromJson((originalRequest).getBody(), domainType);
            } catch (com.google.gson.JsonSyntaxException ex) {
                errorMessages.add(new ErrorMessage(originalRequest.getBody(), Arrays.asList(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ERROR_INVALID_PAYLOAD"), null))));
            }

            validateData(fundSource);

            if (!errorMessages.isEmpty()) {
                FinishRequest finishRequest = new FinishRequest(new Gson().toJson(errorMessages), "text/json", HttpStatus.SC_BAD_REQUEST);
                (originalRequest).getRequestHandler().tell(finishRequest, getSelf());
            } else {
                sendDataToElmis(originalRequest.getBody());
            }

        } else if (msg instanceof MediatorHTTPResponse) { //respond
            log.info("Received response from eLMIS");
            (originalRequest).getRequestHandler().tell(((MediatorHTTPResponse) msg).toFinishRequest(), getSelf());
        } else {
            unhandled(msg);
        }
    }

    /**
     * Handle sending of data to eLMIS
     *
     * @param msg to be sent
     */
    private void sendDataToElmis(String msg) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        String scheme;
        if (config.getProperty("elmis.secure").equals("true")) {
            scheme = "https";
        } else {
            scheme = "http";
        }

        List<Pair<String, String>> params = new ArrayList<>();

        MediatorHTTPRequest forwardToElmisRequest = new MediatorHTTPRequest(
                (originalRequest).getRequestHandler(), getSelf(), "Sending Health Fund Sources to eLMIS", "POST", scheme,
                config.getProperty("elmis.host"), Integer.parseInt(config.getProperty("elmis.api.port")), config.getProperty("elmis.api.health_fund_sources.path"),
                msg, headers, params
        );

        ActorSelection httpConnector = getContext().actorSelection(config.userPathFor("http-connector"));
        httpConnector.tell(forwardToElmisRequest, getSelf());
    }
}