package com.servio.app.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.servio.app.model.Discount;
import com.servio.app.model.ProductState;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long>, JpaSpecificationExecutor<Discount> {
	
	default Page<Discount> searchByFields(String searchParams, List<String> searchFields, Pageable pageable) {
		if (searchParams == null || searchParams.isBlank() || searchFields.isEmpty()) {
			return findAll(pageable);
		}
		Specification<Discount> spec = (root, query, cb) -> cb.disjunction();
		
		for (String field : searchFields) {
			switch (field.toLowerCase()) {
				case "reason":
					spec = spec.or((root, query, criteriaBuilder) ->
							criteriaBuilder.like(criteriaBuilder.lower(root.get("reason")), "%" + searchParams.toLowerCase() + "%"));
					break;
				case "type":
					try {
                        ProductState state = ProductState.valueOf(searchParams.toUpperCase());
                        spec = spec.or((root, query, cb) ->
                            cb.equal(root.get("productState"), state)
                        );
                    } catch (IllegalArgumentException ignored) {}
                    break;
//				case "value":
//					spec = spec.or((root, query, criteriaBuilder) ->
//							criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + searchParams.toLowerCase() + "%"));
//					break;
				default:
					break;
			}
		}
		
		return findAll(spec, pageable); 
		
	}
	
}
