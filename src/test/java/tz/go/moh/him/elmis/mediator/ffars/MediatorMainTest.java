package tz.go.moh.him.elmis.mediator.ffars;

import org.junit.Assert;
import org.junit.Test;
import org.openhim.mediator.engine.MediatorConfig;

import java.lang.reflect.Method;

/**
 * Contains tests for the {@link MediatorMain} class.
 */
public class MediatorMainTest {

    /**
     * Test the mediator main class loading the configuration.
     * @throws Exception
     */
    @Test
    public void testLoadConfig() throws Exception {

        Method loadConfigMethod = MediatorMain.class.getDeclaredMethod("loadConfig", String.class);

        loadConfigMethod.setAccessible(true);
        MediatorConfig mediatorConfig = (MediatorConfig) loadConfigMethod.invoke(null, "src/test/resources/mediator.properties");

        Assert.assertEquals("localhost", mediatorConfig.getServerHost());
        Assert.assertEquals(new Integer(3002), mediatorConfig.getServerPort());
        Assert.assertEquals(new Integer(60000), mediatorConfig.getRootTimeout());
        Assert.assertTrue(mediatorConfig.getHeartsbeatEnabled());
    }
}