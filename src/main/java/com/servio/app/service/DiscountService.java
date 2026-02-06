package com.servio.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.servio.app.dto.DiscountDTO;
import com.servio.app.model.Discount;
import com.servio.app.model.DiscountType;
import com.servio.app.repository.DiscountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiscountService {
	
	private final DiscountRepository discountRepository;
	
	public Page<DiscountDTO> searchDiscounts(String searchParams, List<String> fields, PageRequest pageRequest){
		Page<Discount> discountPage = discountRepository.searchByFields(searchParams, fields, pageRequest);
		
		return discountPage.map(discount -> mapToDTO(discount));
	}
	
	public List<DiscountDTO> getAllDiscounts(){
		List<Discount> discounts = discountRepository.findAll();
		System.out.println("Discounts: " + discounts.size());
		return discounts.stream()
				.map(this::mapToDTO)
				.collect(Collectors.toList());
	}
	
	public DiscountDTO createDiscount(DiscountDTO dto) {
		Discount discount = new Discount();
		discount.setReason(dto.getReason());
		discount.setType(DiscountType.valueOf(dto.getType()));
		discount.setValue(dto.getValue());
		
		Discount saved = discountRepository.save(discount);
		return mapToDTO(saved);
	}
	
	private DiscountDTO mapToDTO(Discount discount) {
		return DiscountDTO.builder()
				.id(discount.getId())
				.reason(discount.getReason())
				.type(discount.getType().name())
				.value(discount.getValue())
				.build();
	}
	
	public List<DiscountDTO> formatDiscountData(Page<DiscountDTO> discountPage){
		return discountPage.getContent().stream().map(this::formatDiscount).collect(Collectors.toList());
	}
	
	private DiscountDTO formatDiscount(DiscountDTO discountDTO) {
		return discountDTO;
	}
	
}
