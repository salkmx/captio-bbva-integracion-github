package com.sngular.captio.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehicleDTO {
	
    @JsonProperty("Id")
    private Long id;

    @JsonProperty("PickupDate")
    private LocalDateTime pickupDate;

    @JsonProperty("ReturnDate")
    private LocalDateTime returnDate;

    @JsonProperty("PickupHour")
    private String pickupHour;

    @JsonProperty("ReturnHour")
    private String returnHour;

    @JsonProperty("PickupPlace")
    private String pickupPlace;

    @JsonProperty("ReturnPlace")
    private String returnPlace;

    @JsonProperty("Type")
    private Integer type;

}
