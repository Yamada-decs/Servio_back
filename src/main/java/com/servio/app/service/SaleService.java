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
import com.servio.app.model.Customer;
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
	
	
	private SaleResponseDTO mapToDTO(Sale sale){
        return SaleResponseDTO.builder()
                .id(sale.getId())
                .customerID(sale.getCustomer().getId())
                .customerName(sale.getCustomer().getName())
                .customerID(sale.getCustomer().getId())
                .total(sale.getTotal())
                .discountReason(sale.getDiscountReason())
                .dicountType(sale.getDiscountType().name())
                .discountAmount(sale.getDiscountAmount())
                .finalPrice(sale.getFinalPrice())
                .remaining(sale.getRemaining())
                .comment(sale.getComment())
                .date(sale.getDate())
                .state(sale.getSaleState().name())
                .build();
    }
	
	
}
