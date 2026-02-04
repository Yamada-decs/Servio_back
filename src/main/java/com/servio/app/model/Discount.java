package com.servio.app.model;

import java.math.BigDecimal;

import jakarta.persistence.*;

public class Discount {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "reason")
	private String reason;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
	private DiscountType type;
	
	@Column(name = "value", nullable = false)
	private BigDecimal value;
	
	
}
