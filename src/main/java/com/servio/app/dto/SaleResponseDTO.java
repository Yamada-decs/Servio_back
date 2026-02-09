package com.servio.app.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleResponseDTO {
	private Long id;
	private Long customerID;
	private String customerName;
	private BigDecimal total;
	private String discountReason;
	private String dicountType;
	private BigDecimal discountAmount;
	private BigDecimal finalPrice;
	private BigDecimal remaining;
	private String comment;
	private LocalDateTime date;
	private String state;
}
