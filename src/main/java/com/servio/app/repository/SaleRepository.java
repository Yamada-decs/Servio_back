package com.servio.app.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.servio.app.model.Sale;
import com.servio.app.model.SaleState;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long>, JpaSpecificationExecutor<Sale>{
	
	List<Sale> findBySaleState(SaleState saleState);
	
	default Page<Sale> searchByFields(String searchParams, List<String> searchFields, Pageable pageable) {
    	if (searchParams == null || searchParams.isBlank() || searchFields.isEmpty()) {
    	    return findAll(pageable);
    	}
    	Specification<Sale> spec = (root, query, cb) -> cb.disjunction();
        
        for (String field : searchFields) {
            switch (field.toLowerCase()) {
                case "customerName":
                    spec = spec.or((root, query, criteriaBuilder) ->
                            criteriaBuilder.like(criteriaBuilder.lower(root.join("customer").get("name")), "%" + searchParams.toLowerCase() + "%"));
                    break;
                case "comment":
                    spec = spec.or((root, query, criteriaBuilder) ->
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("comment")), "%" + searchParams.toLowerCase() + "%"));
                    break;
                case "date":
                	try {
                        LocalDate searchDate = LocalDate.parse(searchParams);

                        LocalDateTime startOfDay = searchDate.atStartOfDay();
                        LocalDateTime endOfDay = searchDate.plusDays(1).atStartOfDay();

                        spec = spec.or((root, query, cb) ->
                            cb.between(root.get("date"), startOfDay, endOfDay)
                        );

                    } catch (Exception ignored) {}
                    break;
//                    spec = spec.or((root, query, criteriaBuilder) ->
//                            criteriaBuilder.like(criteriaBuilder.lower(root.get("date")), "%" + searchParams.toLowerCase() + "%"));
//                    break;
                case "state":
                	try {
                        SaleState state = SaleState.valueOf(searchParams.toUpperCase());
                        spec = spec.or((root, query, cb) ->
                            cb.equal(root.get("saleState"), state)
                        );
                    } catch (IllegalArgumentException ignored) {}
                    break;
                default:
                    break;
            }
        }

        return findAll(spec, pageable); 
        
    }
	
}
