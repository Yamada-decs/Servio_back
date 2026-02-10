package com.servio.app.model;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sale_detail")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaleDetail {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@Column(name = "sale_id")
	private Sale sale;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@Column(name = "product_id")
	private Product product;
	
	@Column(name = "quantity")
	private BigDecimal quantity;
	
	@Column(name = "sub_total")
	private BigDecimal subTotal;
	
}
