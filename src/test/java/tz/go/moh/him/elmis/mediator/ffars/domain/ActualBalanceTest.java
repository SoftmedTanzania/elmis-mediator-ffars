package tz.go.moh.him.elmis.mediator.ffars.domain;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Represents unit tests for the {@link ActualBalance} class.
 */
public class ActualBalanceTest {

    /**
     * Performs an actual balance data serialization test
     */
    @Test
    public void testActualBalanceSerialization() throws Exception {
        ActualBalance actualBalance = new ActualBalance();

        actualBalance.setUid("4590");
        actualBalance.setFacilityCode("newFac");
        actualBalance.setPeriodStartDate("2018/05/01");
        actualBalance.setSourceApplication("FFARS");
        actualBalance.setLineItems(new ArrayList<>());

        BalanceLineItem first = new BalanceLineItem();
        first.setAllocatedBudget(50000);
        first.setAdditive(true);
        first.setFundSourceCode("30D");
        first.setNotes("");
        actualBalance.getLineItems().add(first);

        BalanceLineItem second = new BalanceLineItem();
        second.setAllocatedBudget(7000);
        second.setAdditive(true);
        second.setFundSourceCode("NHIF");
        second.setNotes("");
        actualBalance.getLineItems().add(second);

        Gson gson = new Gson();
        String actual = gson.toJson(actualBalance, ActualBalance.class);

        assertTrue(actual.contains(actualBalance.getUid()));
        assertTrue(actual.contains(actualBalance.getFacilityCode()));
        assertTrue(actual.contains(actualBalance.getPeriodStartDate()));
        assertTrue(actual.contains(actualBalance.getSourceApplication()));

        assertTrue(actual.contains(String.valueOf(actualBalance.getLineItems().get(0).getAllocatedBudget())));
        assertTrue(actual.contains(String.valueOf(actualBalance.getLineItems().get(0).isAdditive())));
        assertTrue(actual.contains(actualBalance.getLineItems().get(0).getFundSourceCode()));
        assertTrue(actual.contains(actualBalance.getLineItems().get(0).getNotes()));

        assertTrue(actual.contains(String.valueOf(actualBalance.getLineItems().get(1).getAllocatedBudget())));
        assertTrue(actual.contains(String.valueOf(actualBalance.getLineItems().get(1).isAdditive())));
        assertTrue(actual.contains(actualBalance.getLineItems().get(1).getFundSourceCode()));
        assertTrue(actual.contains(actualBalance.getLineItems().get(1).getNotes()));
    }

    /**
     * Performs an actual balance data deserialization test
     */
    @Test
    public void testActualBalanceDeserialization() throws Exception {
        InputStream stream = ActualBalanceTest.class.getClassLoader().getResourceAsStream("actual_balance_request.json");
        Assert.assertNotNull(stream);

        ActualBalance actualBalance = new Gson().fromJson(IOUtils.toString(stream), ActualBalance.class);

        assertEquals("4590", actualBalance.getUid());
        assertEquals("newFac", actualBalance.getFacilityCode());
        assertEquals("2018/05/01", actualBalance.getPeriodStartDate());
        assertEquals("FFARS", actualBalance.getSourceApplication());

        assertEquals(2, actualBalance.getLineItems().size());

        assertEquals(50000, actualBalance.getLineItems().get(0).getAllocatedBudget());
        assertEquals(true, actualBalance.getLineItems().get(0).isAdditive());
        assertEquals("30D", actualBalance.getLineItems().get(0).getFundSourceCode());
        assertEquals("", actualBalance.getLineItems().get(0).getNotes());

        assertEquals(7000, actualBalance.getLineItems().get(1).getAllocatedBudget());
        assertEquals(true, actualBalance.getLineItems().get(1).isAdditive());
        assertEquals("NHIF", actualBalance.getLineItems().get(1).getFundSourceCode());
        assertEquals("", actualBalance.getLineItems().get(1).getNotes());
    }
}
