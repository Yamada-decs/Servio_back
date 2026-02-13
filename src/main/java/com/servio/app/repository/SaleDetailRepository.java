package com.servio.app.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.servio.app.model.SaleDetail;


public interface SaleDetailRepository extends JpaRepository<SaleDetail, Long>, JpaSpecificationExecutor<SaleDetail> {
	
	default Page<SaleDetail> searchByFields(String searchParams, List<String> searchFields, Pageable pageable) {
    	if (searchParams == null || searchParams.isBlank() || searchFields.isEmpty()) {
    	    return findAll(pageable);
    	}
    	Specification<SaleDetail> spec = (root, query, cb) -> cb.disjunction();
        
        for (String field : searchFields) {
            switch (field.toLowerCase()) {
                case "sale_id":
                    spec = spec.or((root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(root.get("sale").get("id"), Long.valueOf(searchParams)));
                    break;
//                case "code":
//                    spec = spec.or((root, query, criteriaBuilder) ->
//                            criteriaBuilder.like(criteriaBuilder.lower(root.get("code")), "%" + searchParams.toLowerCase() + "%"));
//                    break;
//                case "state":
//                	try {
//                        ProductState state = ProductState.valueOf(searchParams.toUpperCase());
//                        spec = spec.or((root, query, cb) ->
//                            cb.equal(root.get("productState"), state)
//                        );
//                    } catch (IllegalArgumentException ignored) {}
//                    break;
                default:
                    break;
            }
        }

        return findAll(spec, pageable); 
        
    }
	
}
