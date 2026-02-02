package com.servio.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.servio.app.dto.ProductDTO;
import com.servio.app.repository.ProductRepository;
import com.servio.app.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/servio/products")
@RequiredArgsConstructor
public class ProductController {
	
	private final ProductService productService;
	
	@GetMapping
	public ResponseEntity<List<ProductDTO>> getAllProducts(){
		return ResponseEntity.ok(productService.getAllProducts());
	}
	
	@GetMapping("/list")
	public ResponseEntity<Map<String, Object>> getAllProducts(
            @RequestParam(value = "search_params", required = false, defaultValue = "") String searchParams,
            @RequestParam(value = "search_fields", required = false) String searchFields,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        List<String> fields = searchFields != null ? List.of(searchFields.split(",")) : List.of();
        PageRequest pageRequest = createPageRequest(sort, page, pageSize);

        Page<ProductDTO> productPage = productService.searchProducts(searchParams, fields, pageRequest);

        Map<String, Object> response = new HashMap<>();
        response.put("products", productService.formatProductData(productPage)); 
        response.put("current_page", productPage.getNumber()); 
        response.put("total_pages", productPage.getTotalPages());
        response.put("per_pages", productPage.getSize());
        response.put("total_products", productPage.getTotalElements());

        return ResponseEntity.ok(response);
    }
	
	@PostMapping
	public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO dto) {
        ProductDTO created = productService.createProduct(dto);
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
