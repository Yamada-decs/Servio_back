package com.servio.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.servio.app.dto.SaleDetailDTO;
import com.servio.app.model.Product;
import com.servio.app.model.Sale;
import com.servio.app.model.SaleDetail;
import com.servio.app.repository.ProductRepository;
import com.servio.app.repository.SaleDetailRepository;
import com.servio.app.repository.SaleRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class SaleDetailService {
	
	private final SaleDetailRepository saleDetailRepository;
	private final SaleRepository saleRepository;
	private final SaleService saleService;
	private final ProductRepository productRepository;
	
	public Page<SaleDetailDTO> searchDetails(String searchParams, List<String> fields, PageRequest pageRequest){
		Page<SaleDetail> saleDetailPage = saleDetailRepository.searchByFields(searchParams, fields, pageRequest);
		return saleDetailPage.map(saleDetail -> mapToDTO(saleDetail));
	}
	
	public List<SaleDetailDTO> getAllDetails() {
        List<SaleDetail> details = saleDetailRepository.findAll();
        return details.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
	
	@Transactional
	public SaleDetailDTO createDetail(SaleDetailDTO dto) {
    	SaleDetail detail = new SaleDetail();
    	Sale sale = saleRepository.findById(dto.getSaleId()).orElseThrow(() -> new RuntimeException("Venta no encontrado"));
    	Product product = productRepository.findById(dto.getProductId()).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    	detail.setSale(sale);
    	detail.setProduct(product);
    	detail.setQuantity(dto.getQuantity());
    	detail.setSubTotal(dto.getQuantity().multiply(product.getPrice()));
    	SaleDetail saved = saleDetailRepository.save(detail);
    	saleService.recalculateSale(sale);
    	return mapToDTO(saved);
    }
	
	@Transactional
	public SaleDetailDTO updateDetail(SaleDetailDTO dto, Long id) {
    	SaleDetail detail = saleDetailRepository.findById(id).orElseThrow(() -> new RuntimeException("Detalle no encontrado"));
    	Sale sale = saleRepository.findById(dto.getSaleId()).orElseThrow(() -> new RuntimeException("Venta no encontrado"));
    	Product product = productRepository.findById(dto.getProductId()).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    	detail.setSale(sale);
    	detail.setProduct(product);
    	detail.setQuantity(dto.getQuantity());
    	detail.setSubTotal(dto.getQuantity().multiply(product.getPrice()));
    	//SaleDetail saved = saleDetailRepository.save(detail);
    	saleService.recalculateSale(sale);
    	return mapToDTO(detail);
    }
	
	private SaleDetailDTO mapToDTO(SaleDetail saleDetail){
        return SaleDetailDTO.builder()
                .id(saleDetail.getId())
                .saleId(saleDetail.getSale().getId())
                .productId(saleDetail.getProduct().getId())
                .productName(saleDetail.getProduct().getName())
                .quantity(saleDetail.getQuantity())
                .subTotal(saleDetail.getSubTotal())
                .build();
    }
	
	public List<SaleDetailDTO> formatDetailData(Page<SaleDetailDTO> detailPage) {
        return detailPage.getContent().stream().map(this::formatDetail).collect(Collectors.toList());
    }
	
	private SaleDetailDTO formatDetail(SaleDetailDTO detailDTO) {
        return detailDTO; // Puede agregar lógica adicional si se requiere
    }
	
}
