package com.servio.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.servio.app.dto.DiscountDTO;
import com.servio.app.service.DiscountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/servio/discounts")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService discountService;
	
    @GetMapping
	public ResponseEntity<List<DiscountDTO>> getAllDiscounts(){
		return ResponseEntity.ok(discountService.getAllDiscounts());
	}
    
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getAllDiscounts(
    	@RequestParam(value = "search_params", required = false, defaultValue = "") String searchParams,
        @RequestParam(value = "search_fields", required = false) String searchFields,
        @RequestParam(value = "sort", required = false) String sort,
        @RequestParam(value = "page", defaultValue = "0") int page,	
        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
    	
    	List<String> fields = searchFields != null ? List.of(searchFields.split(",")) : List.of();
    	PageRequest pageRequest = createPageRequest(sort, page, pageSize);

        Page<DiscountDTO> discountPage = discountService.searchDiscounts(searchParams, fields, pageRequest);

        Map<String, Object> response = new HashMap<>();
        response.put("products", discountService.formatDiscountData(discountPage)); 
        response.put("current_page", discountPage.getNumber()); 
        response.put("total_pages", discountPage.getTotalPages());
        response.put("per_pages", discountPage.getSize());
        response.put("total_products", discountPage.getTotalElements());

        return ResponseEntity.ok(response);
    	
    }
    
    @PostMapping
    public ResponseEntity<DiscountDTO> createDiscount(@RequestBody DiscountDTO dto){
    	DiscountDTO created = discountService.createDiscount(dto);
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
