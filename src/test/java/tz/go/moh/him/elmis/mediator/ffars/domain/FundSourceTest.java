package tz.go.moh.him.elmis.mediator.ffars.domain;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Represents unit tests for the {@link FundSource} class.
 */
public class FundSourceTest {
    /**
     * Performs a fund source data serialization test
     */
    @Test
    public void testFundSourceSerialization() throws Exception {
        FundSource fundSource = new FundSource();

        fundSource.setName("NHIF");
        fundSource.setCode("80D");
        fundSource.setDescription("National Health Insurance Fund");
        fundSource.setDisplayOrder("1");
        fundSource.setLastUpdate("2018/02/01");

        Gson gson = new Gson();
        String actual = gson.toJson(fundSource, FundSource.class);

        assertTrue(actual.contains(fundSource.getName()));
        assertTrue(actual.contains(fundSource.getCode()));
        assertTrue(actual.contains(fundSource.getDescription()));
        assertTrue(actual.contains(fundSource.getDisplayOrder()));
        assertTrue(actual.contains(fundSource.getLastUpdate()));
    }

    /**
     * Performs a fund source data deserialization test
     */
    @Test
    public void testFundSourceDeserialization() throws Exception {
        InputStream stream = FundSourceTest.class.getClassLoader().getResourceAsStream("health_fund_source_request.json");
        Assert.assertNotNull(stream);

        FundSource fundSource = new Gson().fromJson(IOUtils.toString(stream), FundSource.class);

        assertEquals("NHIF", fundSource.getName());
        assertEquals("80D", fundSource.getCode());
        assertEquals("National Health Insurance Fund", fundSource.getDescription());
        assertEquals("1", fundSource.getDisplayOrder());
        assertEquals("2018/02/01", fundSource.getLastUpdate());
    }
}
