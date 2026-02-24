package com.servio.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
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
public class ReportSalesDTO {

	private LocalDate date;

    private BigDecimal totalGeneral;
    private BigDecimal totalEfectivo;
    private BigDecimal totalYape;
    //private BigDecimal totalTransferencia;

    private List<SaleReportDTO> sales;
}
