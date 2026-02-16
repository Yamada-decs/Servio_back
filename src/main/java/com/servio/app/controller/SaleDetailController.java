package com.servio.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.servio.app.dto.SaleDetailDTO;
import com.servio.app.service.SaleDetailService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/servio/details")
@RequiredArgsConstructor
public class SaleDetailController {
	
	private final SaleDetailService saleDetailService;
	
	@GetMapping
	public ResponseEntity<List<SaleDetailDTO>> getAllDetails(){
		return ResponseEntity.ok(saleDetailService.getAllDetails());
	}
	
	@GetMapping("/list")
	public ResponseEntity<Map<String, Object>> getAllDetails(
            @RequestParam(value = "search_params", required = false, defaultValue = "") String searchParams,
            @RequestParam(value = "search_fields", required = false) String searchFields,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        List<String> fields = searchFields != null ? List.of(searchFields.split(",")) : List.of();
        PageRequest pageRequest = createPageRequest(sort, page, pageSize);

        Page<SaleDetailDTO> detailPage = saleDetailService.searchDetails(searchParams, fields, pageRequest);

        Map<String, Object> response = new HashMap<>();
        response.put("products", saleDetailService.formatDetailData(detailPage)); 
        response.put("current_page", detailPage.getNumber()); 
        response.put("total_pages", detailPage.getTotalPages());
        response.put("per_pages", detailPage.getSize());
        response.put("total_products", detailPage.getTotalElements());

        return ResponseEntity.ok(response);
    }
	
	@PostMapping
	public ResponseEntity<SaleDetailDTO> createDetail(@RequestBody SaleDetailDTO dto) {
		SaleDetailDTO created = saleDetailService.createDetail(dto);
        return ResponseEntity.ok(created);
    }
	
	@PutMapping("/{id}")
	public ResponseEntity<SaleDetailDTO> updateDetail(@PathVariable Long id ,@RequestBody SaleDetailDTO dto) {
		SaleDetailDTO created = saleDetailService.updateDetail(dto, id);
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
