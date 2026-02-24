package com.servio.app.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.servio.app.model.Payment;
import com.servio.app.model.PaymentMethod;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment>{
	
	default Page<Payment> searchByFields(String searchParams, List<String> searchFields, Pageable pageable) {
    	if (searchParams == null || searchParams.isBlank() || searchFields.isEmpty()) {
    	    return findAll(pageable);
    	}
    	Specification<Payment> spec = (root, query, cb) -> cb.disjunction();
        
        for (String field : searchFields) {
            switch (field.toLowerCase()) {
	            case "sale_id":
	                spec = spec.or((root, query, criteriaBuilder) ->
	                        criteriaBuilder.equal(root.get("sale").get("id"), Long.valueOf(searchParams)));
	                break;
	            case "payment_method":
                	try {
                        PaymentMethod method = PaymentMethod.valueOf(searchParams.toUpperCase());
                        spec = spec.or((root, query, cb) ->
                            cb.equal(root.get("paymentMethod"), method)
                        );
                    } catch (IllegalArgumentException ignored) {}
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
                default:
                    break;
            }
        }

        return findAll(spec, pageable); 
        
    }
	
	@Query("""
		    SELECT p.method, SUM(p.amount)
		    FROM Payment p
		    WHERE p.sale.saleDate BETWEEN :start AND :end
		    GROUP BY p.method
		""")
		List<Object[]> sumByMethodBetween(
		        @Param("start") LocalDateTime start,
		        @Param("end") LocalDateTime end
		);
	
}
