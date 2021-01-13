package tz.go.moh.him.elmis.mediator.ffars.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public class FundSource {
    @SerializedName("name")
    @JsonProperty("name")
    private String name;

    @SerializedName("code")
    @JsonProperty("code")
    private String code;

    @SerializedName("description")
    @JsonProperty("description")
    private String description;

    @SerializedName("displayOrder")
    @JsonProperty("displayOrder")
    private String displayOrder;

    @SerializedName("lastUpdate")
    @JsonProperty("lastUpdate")
    private String lastUpdate;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(String displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
