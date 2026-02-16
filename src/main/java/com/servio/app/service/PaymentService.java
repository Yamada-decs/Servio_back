package com.servio.app.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.servio.app.dto.PaymentDTO;
import com.servio.app.model.Payment;
import com.servio.app.model.PaymentMethod;
import com.servio.app.model.Sale;
import com.servio.app.repository.PaymentRepository;
import com.servio.app.repository.SaleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {
	
	private final PaymentRepository paymentRepository;
	private final SaleRepository saleRepository;
	private final SaleService saleService;
	
	public Page<PaymentDTO> searchPayments(String searchParams, List<String> fields, PageRequest pageRequest){
		Page<Payment> productPage = paymentRepository.searchByFields(searchParams, fields, pageRequest);
		
//		List<ProductDTO> productDTOs = productPage.getContent().stream()
//				.map(this::mapToDTO)
//				.collect(Collectors.toList());
		
		return productPage.map(payment -> mapToDTO(payment));
	}
	
	public List<PaymentDTO> getAllPaymennts() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
	
	@Transactional
	public PaymentDTO createPayment(PaymentDTO dto) {
    	Payment payment = new Payment();
    	Sale sale = saleRepository.findById(dto.getSaleId()).orElseThrow(() -> new RuntimeException("Venta no encontrado"));
    	payment.setSale(sale);
    	payment.setPaymentMethod(PaymentMethod.valueOf(dto.getPaymentMethod()));
    	payment.setAmount(dto.getAmount());
    	if (dto.getDate() != null) {
    		payment.setDate(dto.getDate());
    	} else {
    		payment.setDate(LocalDateTime.now());
    	}
    	Payment saved = paymentRepository.save(payment);
    	saleService.recalculateSale(sale);
    	return mapToDTO(saved);
    }
	
	@Transactional
	public PaymentDTO updatePayment(PaymentDTO dto, Long id) {
		Payment payment = paymentRepository.findById(id).orElseThrow(() -> new RuntimeException("Pago no encontrado"));
    	Sale sale = saleRepository.findById(dto.getSaleId()).orElseThrow(() -> new RuntimeException("Venta no encontrado"));
    	payment.setSale(sale);
    	payment.setPaymentMethod(PaymentMethod.valueOf(dto.getPaymentMethod()));
    	payment.setAmount(dto.getAmount());
    	if (dto.getDate() != null) {
    		payment.setDate(dto.getDate());
    	} else {
    		payment.setDate(LocalDateTime.now());
    	}
    	//Payment saved = paymentRepository.save(payment);
    	saleService.recalculateSale(sale);
    	return mapToDTO(payment);
    }
	
	private PaymentDTO mapToDTO(Payment payment){
        return PaymentDTO.builder()
                .id(payment.getId())
                .saleId(payment.getSale().getId())
                .paymentMethod(payment.getPaymentMethod().name())
                .amount(payment.getAmount())
                .date(payment.getDate())
                .build();
    }
	
	public List<PaymentDTO> formatPaymentData(Page<PaymentDTO> paymentPage) {
        return paymentPage.getContent().stream().map(this::formatPayment).collect(Collectors.toList());
    }
	
	private PaymentDTO formatPayment(PaymentDTO paymentDTO) {
        return paymentDTO; // Puede agregar lógica adicional si se requiere
    }
	
}
