package com.servio.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.servio.app.dto.SaleRequestDTO;
import com.servio.app.dto.SaleResponseDTO;
import com.servio.app.service.SaleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/servio/sales")
@RequiredArgsConstructor
public class SaleController {
	
	private final SaleService saleService;
	
	@GetMapping
	public ResponseEntity<List<SaleResponseDTO>> getAllSales(){
		return ResponseEntity.ok(saleService.getAllSales());
	}
	
	@GetMapping("/list")
	public ResponseEntity<Map<String, Object>> getAllSales(
            @RequestParam(value = "search_params", required = false, defaultValue = "") String searchParams,
            @RequestParam(value = "search_fields", required = false) String searchFields,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        List<String> fields = searchFields != null ? List.of(searchFields.split(",")) : List.of();
        PageRequest pageRequest = createPageRequest(sort, page, pageSize);

        Page<SaleResponseDTO> salePage = saleService.searchSales(searchParams, fields, pageRequest);

        Map<String, Object> response = new HashMap<>();
        response.put("sales", saleService.formatSaleData(salePage)); 
        response.put("current_page", salePage.getNumber()); 
        response.put("total_pages", salePage.getTotalPages());
        response.put("per_pages", salePage.getSize());
        response.put("total_products", salePage.getTotalElements());

        return ResponseEntity.ok(response);
    }
	
	@PostMapping
	public ResponseEntity<SaleResponseDTO> createProduct(@RequestBody SaleRequestDTO dto) {
        SaleResponseDTO created = saleService.createSale(dto);
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
