package com.servio.app.model;



import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sale")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Sale {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@Column(name = "customer_id")
	private Customer customer;
	
	@Column(name = "total", nullable = false)
	private BigDecimal total;
	
	@Column(name = "discount_reason")
	private String discountReason;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "discount_type")
	private DiscountType discountType;
	
	@Column(name = "discount_amount")
	private BigDecimal discountAmount;
	
	@Column(name = "final_price")
	private BigDecimal finalPrice;
	
	@Column(name = "remaining")
	private BigDecimal remaining;
	
	@Column(name = "comment")
	private String comment;
	
	@Column(name = "date", nullable = false)
	private LocalDateTime date;
	
	@Column(name = "state")
	private SaleState saleState;
	
}
