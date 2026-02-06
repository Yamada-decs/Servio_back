package com.servio.app.model;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "discount")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
