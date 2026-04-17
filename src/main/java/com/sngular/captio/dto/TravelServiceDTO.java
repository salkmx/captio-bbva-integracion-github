package com.sngular.captio.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TravelServiceDTO {

	@JsonProperty("Id")
	private Long id;

	@JsonProperty("Flights")
	private List<FlightDTO> flights;

	@JsonProperty("FlightComments")
	private String flightComments;

	@JsonProperty("Trains")
	private List<TrainDTO> trains;

	@JsonProperty("TrainComments")
	private String trainComments;

	@JsonProperty("Vehicles")
	private List<VehicleDTO> vehicles;

	@JsonProperty("VehicleComments")
	private String vehicleComments;

	@JsonProperty("Ships")
	private List<ShipDTO> ships;

	@JsonProperty("ShipComments")
	private String shipComments;

	@JsonProperty("Hotels")
	private List<HotelDTO> hotels;

	@JsonProperty("HotelComments")
	private String hotelComments;

	@JsonProperty("Others")
	private List<OtherDTO> others;

}
