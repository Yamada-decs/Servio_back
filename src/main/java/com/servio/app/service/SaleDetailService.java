package com.servio.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.servio.app.dto.SaleDetailDTO;
import com.servio.app.model.SaleDetail;
import com.servio.app.repository.SaleDetailRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class SaleDetailService {
	
	private final SaleDetailRepository saleDetailRepository;
	
	public Page<SaleDetailDTO> searchProducts(String searchParams, List<String> fields, PageRequest pageRequest){
		Page<SaleDetail> saleDetailPage = saleDetailRepository.searchByFields(searchParams, fields, pageRequest);
		return saleDetailPage.map(saleDetail -> mapToDTO(saleDetail));
	}
	
	
	
	
	
	private SaleDetailDTO mapToDTO(SaleDetail saleDetail){
        return SaleDetailDTO.builder()
                .id(saleDetail.getId())
                .saleId(saleDetail.getSale().getId())
                .productId(saleDetail.getProduct().getId())
                .quantity(saleDetail.getQuantity())
                .subTotal(saleDetail.getSubTotal())
                .build();
    }
	
}
