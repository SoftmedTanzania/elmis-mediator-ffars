package tz.go.moh.him.elmis.mediator.ffars.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public class BalanceLineItem {
    @SerializedName("allocatedBudget")
    @JsonProperty("allocatedBudget")
    private int allocatedBudget;

    @SerializedName("additive")
    @JsonProperty("additive")
    private boolean additive;

    @SerializedName("fundSourceCode")
    @JsonProperty("fundSourceCode")
    private String fundSourceCode;

    @SerializedName("notes")
    @JsonProperty("notes")
    private String notes;

    public int getAllocatedBudget() {
        return allocatedBudget;
    }

    public void setAllocatedBudget(int allocatedBudget) {
        this.allocatedBudget = allocatedBudget;
    }

    public boolean isAdditive() {
        return additive;
    }

    public void setAdditive(boolean additive) {
        this.additive = additive;
    }

    public String getFundSourceCode() {
        return fundSourceCode;
    }

    public void setFundSourceCode(String fundSourceCode) {
        this.fundSourceCode = fundSourceCode;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
