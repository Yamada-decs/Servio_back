package com.servio.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.servio.app.dto.CustomerDTO;
import com.servio.app.service.CustomerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/servio/customers")
@RequiredArgsConstructor
public class CustomerController {

	private final CustomerService customerService;
	
	@GetMapping
	public ResponseEntity<List<CustomerDTO>> getAllCustomers(){
		return ResponseEntity.ok(customerService.getAllCustomers());
	}
	
	@GetMapping("/list")
	public ResponseEntity<Map<String, Object>> getallCustomers(
            @RequestParam(value = "search_params", required = false, defaultValue = "") String searchParams,
            @RequestParam(value = "search_fields", required = false) String searchFields,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        List<String> fields = searchFields != null ? List.of(searchFields.split(",")) : List.of();
        PageRequest pageRequest = createPageRequest(sort, page, pageSize);

        Page<CustomerDTO> customerPage = customerService.searchCustomers(searchParams, fields, pageRequest);

        Map<String, Object> response = new HashMap<>();
        response.put("products", customerService.formatCustomerData(customerPage)); 
        response.put("current_page", customerPage.getNumber()); 
        response.put("total_pages", customerPage.getTotalPages());
        response.put("per_pages", customerPage.getSize());
        response.put("total_products", customerPage.getTotalElements());

        return ResponseEntity.ok(response);
    }
	
	@PostMapping
	public ResponseEntity<CustomerDTO> createCustomer (@RequestBody CustomerDTO dto){
		CustomerDTO created = customerService.createCustomer(dto);
		return ResponseEntity.ok(created);
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
