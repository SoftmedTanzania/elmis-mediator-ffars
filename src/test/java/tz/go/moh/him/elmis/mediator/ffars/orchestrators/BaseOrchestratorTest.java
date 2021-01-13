package tz.go.moh.him.elmis.mediator.ffars.orchestrators;

import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.testing.TestingUtils;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

public class BaseOrchestratorTest {
    /**
     * Represents the configuration.
     */
    protected static MediatorConfig configuration;

    /**
     * Represents the system actor.
     */
    protected static ActorSystem system;

    /**
     * Represents the error messages resource.
     */
    protected static JSONObject errorMessageResource;

    /**
     * Runs cleanup after each test execution.
     */
    @After
    public void after() {
        TestingUtils.clearRootContext(system, configuration.getName());
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }

    /**
     * Runs cleanup nefpre each test execution.
     */
    @Before
    public void before() throws Exception {
        try {
            configuration = loadConfig(null);
            system = ActorSystem.create();

            InputStream stream = getClass().getClassLoader().getResourceAsStream("error-messages.json");
            if (stream != null) {
                errorMessageResource = new JSONObject(IOUtils.toString(stream));
            }
        } catch (Exception e) {
            // TODO: handle this issue
            return;
        }
    }

    /**
     * Loads the mediator configuration.
     *
     * @param configPath The configuration path.
     * @return Returns the mediator configuration.
     */
    public static MediatorConfig loadConfig(String configPath) {
        MediatorConfig config = new MediatorConfig();


        try {
            if (configPath != null) {
                Properties props = new Properties();
                File conf = new File(configPath);
                InputStream in = FileUtils.openInputStream(conf);
                props.load(in);
                IOUtils.closeQuietly(in);

                config.setProperties(props);
            } else {
                config.setProperties("mediator.properties");
            }
        } catch (Exception e) {
            // TODO: handle this issue
        }

        config.setName(config.getProperty("mediator.name"));
        config.setServerHost(config.getProperty("mediator.host"));
        config.setServerPort(Integer.parseInt(config.getProperty("mediator.port")));
        config.setRootTimeout(Integer.parseInt(config.getProperty("mediator.timeout")));

        config.setCoreHost(config.getProperty("core.host"));
        config.setCoreAPIUsername(config.getProperty("core.api.user"));
        config.setCoreAPIPassword(config.getProperty("core.api.password"));

        config.setCoreAPIPort(Integer.parseInt(config.getProperty("core.api.port")));
        config.setHeartbeatsEnabled(true);

        return config;
    }
}
