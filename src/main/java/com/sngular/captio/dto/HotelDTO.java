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
public class HotelDTO {

	@JsonProperty("Id")
	private Long id;

	@JsonProperty("CheckInDate")
	private LocalDateTime checkInDate;

	@JsonProperty("CheckOutDate")
	private LocalDateTime checkOutDate;

	@JsonProperty("Name")
	private String name;

	@JsonProperty("Location")
	private String location;

	@JsonProperty("RoomType")
	private Integer roomType;

	@JsonProperty("BoardType")
	private Integer boardType;

	@JsonProperty("Category")
	private Integer category;

}
