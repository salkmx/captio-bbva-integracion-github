package com.sngular.captio.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(toBuilder = true)
public class GastoDTO {

	@JsonProperty("Id")
	private Integer id;

	@JsonProperty("ExternalId")
	private String externalId;

	@JsonProperty("Date")
	private LocalDateTime date;

	@JsonProperty("CreationDate")
	private LocalDateTime creationDate;

	@JsonProperty("Merchant")
	private String merchant;

	@JsonProperty("ExpenseAmount")
	private MontoDTO expenseAmount;

	@JsonProperty("FinalAmount")
	private MontoDTO finalAmount;

	@JsonProperty("Comment")
	private String comment;

	@JsonProperty("IsMileage")
	private Boolean isMileage;

	@JsonProperty("Reconciled")
	private Boolean reconciled;

	@JsonProperty("ReconciledPayments")
	private Integer reconciledPayments;

	@JsonProperty("RejectedByReviewer")
	private Boolean rejectedByReviewer;

	@JsonProperty("UrlKey")
	private String urlKey;

	@JsonProperty("Attachments")
	private List<AdjuntoDTO> attachments;

	@JsonProperty("RepositoryId")
	private String repositoryId;

	@JsonProperty("Category")
	private CategoriaDTO category;

	@JsonProperty("PaymentMethod")
	private MetodoPagoDTO paymentMethod;

	@JsonProperty("Invoice")
	private FacturaDTO invoice;

	@JsonProperty("InvoiceReviewed")
	private Boolean invoiceReviewed;

	@JsonProperty("VATExempt")
	private Boolean vatExempt;

	@JsonProperty("VATExemptAmount")
	private Double vatExemptAmount;

	@JsonProperty("VatRates")
	private List<TasaIvaDTO> vatRates;

	@JsonProperty("TIN")
	private String tin;

	@JsonProperty("InvoiceNumber")
	private String invoiceNumber;

	@JsonProperty("Series")
	private String series;

	@JsonProperty("UUID")
	private String uuid;

	@JsonProperty("CustomFields")
	private List<CustomFieldDTO> customFields;

	@JsonProperty("User")
	private SimpleIdDTO user;

	@JsonProperty("Report")
	private SimpleIdDTO report;

	@JsonProperty("Payment")
	private SimpleIdDTO payment;

	@JsonProperty("MileageInfo")
	private MileageInfoDTO mileageInfo;

	@JsonProperty("ExpenseType")
	private Integer expenseType;

	@JsonProperty("PerDiemInfo")
	private PerDiemInfoDTO perDiemInfo;

	@JsonProperty("UserId")
	private Integer userId;
	
	private String usuarioCorporativo;
	
	private String nombreInforme;

	public GastoDTO(GastoDTO other) {

		this.id = other.id;
		this.externalId = other.externalId;
		this.date = other.date;
		this.creationDate = other.creationDate;
		this.merchant = other.merchant;

		this.expenseAmount = other.expenseAmount;
		this.finalAmount = other.finalAmount;

		this.comment = other.comment;
		this.isMileage = other.isMileage;
		this.reconciled = other.reconciled;
		this.reconciledPayments = other.reconciledPayments;
		this.rejectedByReviewer = other.rejectedByReviewer;
		this.urlKey = other.urlKey;

		this.attachments = other.attachments != null ? new ArrayList<>(other.attachments) : null;

		this.repositoryId = other.repositoryId;
		this.category = other.category;
		this.paymentMethod = other.paymentMethod;
		this.invoice = other.invoice;

		this.invoiceReviewed = other.invoiceReviewed;
		this.vatExempt = other.vatExempt;
		this.vatExemptAmount = other.vatExemptAmount;

		this.vatRates = other.vatRates != null ? new ArrayList<>(other.vatRates) : null;

		this.tin = other.tin;
		this.invoiceNumber = other.invoiceNumber;
		this.series = other.series;
		this.uuid = other.uuid;

		this.customFields = other.customFields != null ? new ArrayList<>(other.customFields) : null;

		this.user = other.user;
		this.report = other.report;
		this.payment = other.payment;
		this.mileageInfo = other.mileageInfo;

		this.expenseType = other.expenseType;
		this.perDiemInfo = other.perDiemInfo;
		this.userId = other.userId;
	}

}
