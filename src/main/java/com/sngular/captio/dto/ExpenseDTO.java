package com.sngular.captio.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sngular.captio.util.DateUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExpenseDTO {

	@JsonProperty("UserId")
	private Integer userId;

	@JsonProperty("RepositoryId")
	private String repositoryId;

	@JsonProperty("CurrencyId")
	private Integer currencyId;

	@JsonProperty("PaymentMethodId")
	private Integer paymentMethodId;

	@JsonProperty("ProjectId")
	private Integer projectId;

	@JsonProperty("CategoryId")
	private Integer categoryId;

	@JsonProperty("Amount")
	private Double amount;

	@JsonProperty("Merchant")
	private String merchant;

	@JsonIgnore
	private LocalDateTime date;

	@JsonProperty("Comment")
	private String comment;

	@JsonProperty("CustomFields")
	private List<CustomFieldDTO> customFields;

	@JsonProperty("VATExempt")
	private Boolean vatExempt;

	@JsonProperty("VATExemptAmount")
	private Double vatExemptAmount;

	@JsonProperty("TIN")
	private String tin;

	@JsonProperty("InvoiceNumber")
	private String invoiceNumber;

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	@JsonProperty("Date")
	public String getDateFormatted() {
		return date == null ? null : DateUtils.obtenerFechaInicialServicios(this.date);
	}
}
