package com.servio.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.servio.app.dto.ProductDTO;
import com.servio.app.model.Product;
import com.servio.app.model.ProductState;
import com.servio.app.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
	
	private final ProductRepository productRepository;
	
	public Page<ProductDTO> searchProducts(String searchParams, List<String> fields, PageRequest pageRequest){
		Page<Product> productPage = productRepository.searchByFields(searchParams, fields, pageRequest);
		
//		List<ProductDTO> productDTOs = productPage.getContent().stream()
//				.map(this::mapToDTO)
//				.collect(Collectors.toList());
		
		return productPage.map(product -> mapToDTO(product));
	}
	
//  FUTURO PARA PAGINAR
//	public List<ProductDTO> getAllProductsList(int page, int pageSize) {
//        Pageable pageable = PageRequest.of(page, pageSize);
//        List<Product> activeProducts = productRepository.findByProductState(ProductState.ACTIVO);
//        System.out.println("Active Products: " + activeProducts.size());
//        return activeProducts.stream()
//                .map(this::mapToDTO)
//                .collect(Collectors.toList());
//    }

    public List<ProductDTO> getAllProducts() {
        List<Product> activeProducts = productRepository.findByProductState(ProductState.ACTIVO);
        return activeProducts.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public ProductDTO createProduct(ProductDTO dto) {
    	Product product = new Product();
    	product.setName(dto.getName());
    	product.setCode(dto.getCode());
    	product.setPrice(dto.getPrice());
    	product.setProductState(ProductState.valueOf(dto.getState()));
    	
    	Product saved = productRepository.save(product);
    	return mapToDTO(saved);
    }
	
	
	private ProductDTO mapToDTO(Product product){
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .code(product.getCode())
                .price(product.getPrice())
                .state(product.getProductState().name())
                .build();
    }
	
	public List<ProductDTO> formatProductData(Page<ProductDTO> productPage) {
        return productPage.getContent().stream().map(this::formatProduct).collect(Collectors.toList());
    }
	
	private ProductDTO formatProduct(ProductDTO productDTO) {
        return productDTO; // Puede agregar lógica adicional si se requiere
    }
	
	
	
}
