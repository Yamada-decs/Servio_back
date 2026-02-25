package com.servio.app.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.servio.app.dto.ReportSalesDTO;
import com.servio.app.dto.SaleReportDTO;
import com.servio.app.dto.SaleDetailDTO;
import com.servio.app.dto.PaymentDTO;
import com.servio.app.model.PaymentMethod;
import com.servio.app.model.Sale;
import com.servio.app.repository.PaymentRepository;
import com.servio.app.repository.SaleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {
	
	private final SaleRepository saleRepository;
    private final PaymentRepository paymentRepository;

	public ReportSalesDTO generateReport(LocalDate date) {

        // 1️⃣ Traer ventas del día
		LocalDateTime start = date.atStartOfDay();
		LocalDateTime end = date.atTime(23, 59, 59);
        List<Sale> sales = saleRepository.findByDateBetween(start, end);

        // 2️⃣ Mapear ventas a DTO
        List<SaleReportDTO> salesDTO = sales.stream()
                .map(this::mapToSaleReportDTO)
                .toList();

        // 3️⃣ Total general (de ventas)
        BigDecimal totalGeneral = sales.stream()
                .map(Sale::getFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4️⃣ Totales por método (QUERY OPTIMIZADA)
        List<Object[]> results = paymentRepository.sumByMethodBetween(start, end);

        BigDecimal totalEfectivo = BigDecimal.ZERO;
        BigDecimal totalYape = BigDecimal.ZERO;
        BigDecimal totalTransferencia = BigDecimal.ZERO;

        for (Object[] row : results) {

        	PaymentMethod method = (PaymentMethod) row[0];
            BigDecimal amount = (BigDecimal) row[1];

            switch (method) {
                case EFECTIVO -> totalEfectivo = amount;
                case YAPE -> totalYape = amount;
                //case TRANSFERENCIA -> totalTransferencia = amount;
            }
        }

        // 5️⃣ Construir reporte
        ReportSalesDTO report = new ReportSalesDTO();
        report.setDate(date);
        report.setTotalGeneral(totalGeneral);
        report.setTotalEfectivo(totalEfectivo);
        report.setTotalYape(totalYape);
        //report.setTotalTransferencia(totalTransferencia);
        report.setSales(salesDTO);

        return report;
    }
	private SaleReportDTO mapToSaleReportDTO(Sale sale){
		// 🔹 Mapear detalles
	    List<SaleDetailDTO> detailDTOs = sale.getDetails()
	            .stream()
	            .map(detail -> {
	                SaleDetailDTO dto = new SaleDetailDTO();
	                dto.setProductName(detail.getProduct().getName());
	                dto.setUnitPrice(detail.getProduct().getPrice());
	                dto.setQuantity(detail.getQuantity());
	                dto.setSubTotal(detail.getSubTotal());
	                return dto;
	            })
	            .toList();
	
	    // 🔹 Mapear pagos
	    List<PaymentDTO> paymentDTOs = sale.getPayments()
	            .stream()
	            .map(payment -> {
	            	PaymentDTO dto = new PaymentDTO();
	                dto.setPaymentMethod(payment.getPaymentMethod().name());
	                dto.setAmount(payment.getAmount());
	                return dto;
	            })
	            .toList();
	
        return SaleReportDTO.builder()
                .id(sale.getId())
                .customerName(sale.getCustomer() != null 
                        ? sale.getCustomer().getName() 
                                : null)
                .deliveryAddress(sale.getDeliveryAddress())
                .deliveryPrice(sale.getDeliveryPrice())
                .total(sale.getTotal())
                .dicountType(sale.getDiscountType() != null 
                        ? sale.getDiscountType().name() 
                                : null)
                .discountAmount(sale.getDiscountAmount())
                .finalPrice(sale.getFinalPrice())
                .remaining(sale.getRemaining())
                .comment(sale.getComment())
                .state(sale.getSaleState().name())
                .details(detailDTOs)
                .payments(paymentDTOs)
                .build();
    }
	
}
