package com.servio.app.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.servio.app.model.Product;
import com.servio.app.model.ProductState;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product>{
	
	List<Product> findByProductState(ProductState productState);

    default Page<Product> searchByFields(String searchParams, List<String> searchFields, Pageable pageable) {
    	if (searchParams == null || searchParams.isBlank() || searchFields.isEmpty()) {
    	    return findAll(pageable);
    	}
    	Specification<Product> spec = (root, query, cb) -> cb.disjunction();
        
        for (String field : searchFields) {
            switch (field.toLowerCase()) {
                case "name":
                    spec = spec.or((root, query, criteriaBuilder) ->
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + searchParams.toLowerCase() + "%"));
                    break;
                case "code":
                    spec = spec.or((root, query, criteriaBuilder) ->
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("code")), "%" + searchParams.toLowerCase() + "%"));
                    break;
                case "state":
                	try {
                        ProductState state = ProductState.valueOf(searchParams.toUpperCase());
                        spec = spec.or((root, query, cb) ->
                            cb.equal(root.get("productState"), state)
                        );
                    } catch (IllegalArgumentException ignored) {}
                    break;
                default:
                    break;
            }
        }

        return findAll(spec, pageable); 
        
    }

    @Query("""
    	    SELECT COUNT(p)
    	    FROM Product p
    	    WHERE
    	        LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))
    	        OR LOWER(p.code) LIKE LOWER(CONCAT('%', :search, '%'))
    	        OR CAST(p.productState AS string) LIKE LOWER(CONCAT('%', :search, '%'))
    	""")
    long countBySearch(String search);
    
}
