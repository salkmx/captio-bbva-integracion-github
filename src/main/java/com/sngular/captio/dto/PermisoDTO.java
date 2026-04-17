package com.sngular.captio.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PermisoDTO {
	
    @JsonProperty("Alerts")
    private Boolean alerts;

    @JsonProperty("DeleteReceipt")
    private Boolean deleteReceipt;

    @JsonProperty("EditReceipt")
    private Boolean editReceipt;

    @JsonProperty("Email")
    private Boolean email;

    @JsonProperty("SkipStep")
    private Boolean skipStep;

    @JsonProperty("AdvancesSettings")
    private Boolean advancesSettings;

}
