package com.servio.app.dto;

import java.math.BigDecimal;

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
public class SaleDetailDTO {
	private Long id;
	private Long saleId;
	private Long productId;
	private BigDecimal quantity;
	private BigDecimal subTotal;
}
