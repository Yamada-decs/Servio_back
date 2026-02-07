package com.servio.app.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.servio.app.model.Customer;
import com.servio.app.model.CustomerState;

public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
	
	List<Customer> findByCustomerState(CustomerState state);
	
	default Page<Customer> searchByFields(String searchParams, List<String> searchFields, Pageable pageable) {
    	if (searchParams == null || searchParams.isBlank() || searchFields.isEmpty()) {
    	    return findAll(pageable);
    	}
    	Specification<Customer> spec = (root, query, cb) -> cb.disjunction();
        
        for (String field : searchFields) {
            switch (field.toLowerCase()) {
                case "name":
                    spec = spec.or((root, query, criteriaBuilder) ->
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + searchParams.toLowerCase() + "%"));
                    break;
                case "comment":
                    spec = spec.or((root, query, criteriaBuilder) ->
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("comment")), "%" + searchParams.toLowerCase() + "%"));
                    break;
                case "state":
                	try {
                        CustomerState state = CustomerState.valueOf(searchParams.toUpperCase());
                        spec = spec.or((root, query, cb) ->
                            cb.equal(root.get("customerState"), state)
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
