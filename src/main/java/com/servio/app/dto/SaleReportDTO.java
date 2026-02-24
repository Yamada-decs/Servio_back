package com.servio.app.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
public class SaleReportDTO {
	private Long id;

    private String customerName;

    private BigDecimal total;
    private BigDecimal discountAmount;
    private String dicountType;
    private BigDecimal finalPrice;
    private BigDecimal remaining;
    
    private String comment;
    private String deliveryAddress;
	private BigDecimal deliveryPrice;

    private String state;

    private List<SaleDetailDTO> details;
    private List<PaymentDTO> payments;
	
}
