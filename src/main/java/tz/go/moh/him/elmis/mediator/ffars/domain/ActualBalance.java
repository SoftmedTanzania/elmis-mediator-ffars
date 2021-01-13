package tz.go.moh.him.elmis.mediator.ffars.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ActualBalance {
    @SerializedName("UID")
    @JsonProperty("UID")
    private String uid;

    @SerializedName("facilityCode")
    @JsonProperty("facilityCode")
    private String facilityCode;

    @SerializedName("periodStartDate")
    @JsonProperty("periodStartDate")
    private String periodStartDate;

    @SerializedName("sourceApplication")
    @JsonProperty("sourceApplication")
    private String sourceApplication;

    @SerializedName("lineItem")
    @JsonProperty("lineItem")
    private List<BalanceLineItem> lineItems;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFacilityCode() {
        return facilityCode;
    }

    public void setFacilityCode(String facilityCode) {
        this.facilityCode = facilityCode;
    }

    public String getPeriodStartDate() {
        return periodStartDate;
    }

    public void setPeriodStartDate(String periodStartDate) {
        this.periodStartDate = periodStartDate;
    }

    public String getSourceApplication() {
        return sourceApplication;
    }

    public void setSourceApplication(String sourceApplication) {
        this.sourceApplication = sourceApplication;
    }

    public List<BalanceLineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<BalanceLineItem> lineItems) {
        this.lineItems = lineItems;
    }
}
