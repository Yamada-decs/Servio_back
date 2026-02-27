package com.servio.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.servio.app.dto.PaymentDTO;
import com.servio.app.repository.PaymentRepository;
import com.servio.app.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/servio/payments")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;

	
	@GetMapping
	public ResponseEntity<List<PaymentDTO>> getAllPayments(){
		return ResponseEntity.ok(paymentService.getAllPaymennts());
	}
	
	@GetMapping("/list")
	public ResponseEntity<Map<String, Object>> getAllPayments(
            @RequestParam(value = "search_params", required = false, defaultValue = "") String searchParams,
            @RequestParam(value = "search_fields", required = false) String searchFields,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        List<String> fields = searchFields != null ? List.of(searchFields.split(",")) : List.of();
        PageRequest pageRequest = createPageRequest(sort, page, pageSize);

        Page<PaymentDTO> paymentPage = paymentService.searchPayments(searchParams, fields, pageRequest);

        Map<String, Object> response = new HashMap<>();
        response.put("sales", paymentService.formatPaymentData(paymentPage)); 
        response.put("current_page", paymentPage.getNumber()); 
        response.put("total_pages", paymentPage.getTotalPages());
        response.put("per_pages", paymentPage.getSize());
        response.put("total_products", paymentPage.getTotalElements());

        return ResponseEntity.ok(response);
    }
	
	@PostMapping
	public ResponseEntity<PaymentDTO> createPayment(@RequestBody PaymentDTO dto) {
		PaymentDTO created = paymentService.createPayment(dto);
        return ResponseEntity.ok(created);
    }
	
	@PutMapping("/{id}")
	public ResponseEntity<PaymentDTO> updatePayment(@PathVariable Long id, @RequestBody PaymentDTO dto) {
		PaymentDTO created = paymentService.updatePayment(dto, id);
        return ResponseEntity.ok(created);
    }
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletePayment(@PathVariable Long id){
		paymentService.deletePayment(id);
		return ResponseEntity.noContent().build();
	}
	
	private PageRequest createPageRequest(String sort, int page, int pageSize) {
        if (sort != null && sort.contains("%")) {
            String[] sortArray = sort.split("%");
            String field = sortArray[0];
            String order = sortArray[1].toUpperCase();
            
            if (order.equals("ASC")) {
                return PageRequest.of(page, pageSize, Sort.by(Sort.Order.asc(field)));
            } else if (order.equals("DESC")) {
                return PageRequest.of(page, pageSize, Sort.by(Sort.Order.desc(field)));
            }
        }
        return PageRequest.of(page, pageSize, Sort.by(Sort.Order.desc("id")));
    }
	
	
}
