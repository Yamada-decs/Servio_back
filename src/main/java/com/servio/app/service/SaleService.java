package com.servio.app.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.servio.app.dto.SaleRequestDTO;
import com.servio.app.dto.SaleResponseDTO;
import com.servio.app.dto.SaleUpdateDTO;
import com.servio.app.model.Customer;
import com.servio.app.model.DiscountType;
import com.servio.app.model.Sale;
import com.servio.app.model.SaleState;
import com.servio.app.repository.CustomerRepository;
import com.servio.app.repository.SaleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SaleService {
	
	private final SaleRepository saleRepository;
	private final CustomerRepository customerRepository;
	
	public Page<SaleResponseDTO> searchSales(String searchParams, List<String> fields, PageRequest pageRequest){
		Page<Sale> salePage = saleRepository.searchByFields(searchParams, fields, pageRequest);
		return salePage.map(sale -> mapToDTO(sale));
	}
	
	public List<SaleResponseDTO> getAllSales() {
        List<Sale> activeSales = saleRepository.findAll();
        return activeSales.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
	
	public SaleResponseDTO createSale(SaleRequestDTO dto) {
    	Sale sale = new Sale();
	    if (dto.getCustomerID() != null) {
	        Customer customer = customerRepository.findById(dto.getCustomerID())
	                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
	        sale.setCustomer(customer);
	    }
    	sale.setTotal(BigDecimal.ZERO);
    	sale.setDiscountAmount(BigDecimal.ZERO);
    	sale.setFinalPrice(BigDecimal.ZERO);
    	sale.setRemaining(BigDecimal.ZERO);
    	sale.setComment(dto.getComment());
    	if (dto.getDate() != null) {
    	    sale.setDate(dto.getDate());
    	} else {
    	    sale.setDate(LocalDateTime.now());
    	}
    	sale.setSaleState(SaleState.DEUDA);	
    	Sale saved = saleRepository.save(sale);
    	return mapToDTO(saved);
    }
	
	public SaleResponseDTO updateSale(SaleUpdateDTO dto, Long id) {
    	Sale sale = saleRepository.findById(id).orElseThrow(() -> new RuntimeException("Venta no encontrada"));;
	    if (dto.getCustomerID() != null) {
	        Customer customer = customerRepository.findById(dto.getCustomerID())
	                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
	        sale.setCustomer(customer);
	    }
	    if (dto.getDiscountAmount() != null) {
	        sale.setDiscountAmount(dto.getDiscountAmount());
	        sale.setDiscountReason(dto.getDiscountReason());
	    	sale.setDiscountType(DiscountType.valueOf(dto.getDiscountType()));
	    	recalculateSale(sale);
	    }
    	sale.setComment(dto.getComment());
    	Sale saved = saleRepository.save(sale);
    	return mapToDTO(saved);
    }
	
	
	private void recalculateSale(Sale sale) {
	    
	    
	    BigDecimal total = sale.getDetails().stream()
	            .map(detail -> detail.getSubTotal())
	            .reduce(BigDecimal.ZERO, BigDecimal::add);
	    sale.setTotal(total);


	    BigDecimal finalPrice = total;

	    if (sale.getDiscountAmount() != null && sale.getDiscountType() != null) {

	        if (sale.getDiscountType() == DiscountType.DINERO) {
	            finalPrice = total.subtract(sale.getDiscountAmount());
	        }

	        if (sale.getDiscountType() == DiscountType.PORCENTAJE) {
	            BigDecimal porcentaje = sale.getDiscountAmount()
	                    .divide(BigDecimal.valueOf(100));
	            finalPrice = total.subtract(total.multiply(porcentaje));
	        }
	    }

	    if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
	        finalPrice = BigDecimal.ZERO;
	    }

	    sale.setFinalPrice(finalPrice);


	    BigDecimal totalPagado = sale.getPayments().stream()
	            .map(payment -> payment.getAmount())
	            .reduce(BigDecimal.ZERO, BigDecimal::add);

	    BigDecimal remaining = finalPrice.subtract(totalPagado);

	    if (remaining.compareTo(BigDecimal.ZERO) < 0) {
	        remaining = BigDecimal.ZERO;
	    }
	    sale.setRemaining(remaining);

	    
	    if (remaining.compareTo(BigDecimal.ZERO) == 0) {
	        sale.setSaleState(SaleState.PAGADO);
	    } else if (totalPagado.compareTo(BigDecimal.ZERO) > 0) {
	        sale.setSaleState(SaleState.PARCIAL);
	    } else {
	        sale.setSaleState(SaleState.DEUDA);
	    }
	}
	
	
	private SaleResponseDTO mapToDTO(Sale sale){
        return SaleResponseDTO.builder()
                .id(sale.getId())
                .customerID(sale.getCustomer() != null 
                        ? sale.getCustomer().getId() 
                                : null)
                .customerName(sale.getCustomer() != null 
                        ? sale.getCustomer().getName() 
                                : null)
                .total(sale.getTotal())
                .discountReason(sale.getDiscountReason())
                .dicountType(sale.getDiscountType() != null 
                        ? sale.getDiscountType().name() 
                                : null)
                .discountAmount(sale.getDiscountAmount())
                .finalPrice(sale.getFinalPrice())
                .remaining(sale.getRemaining())
                .comment(sale.getComment())
                .date(sale.getDate())
                .state(sale.getSaleState().name())
                .build();
    }
	
	public List<SaleResponseDTO> formatSaleData(Page<SaleResponseDTO> salePage) {
        return salePage.getContent().stream().map(this::formatSale).collect(Collectors.toList());
    }
	
	private SaleResponseDTO formatSale(SaleResponseDTO saleDTO) {
        return saleDTO; // Puede agregar lógica adicional si se requiere
    }
	
	
	
}
