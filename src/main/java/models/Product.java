package models;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;

@Entity
@Table(name = "product")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "name", nullable = false, unique = true)
	private String name;
	
	@Column(name = "code", nullable = false, unique = true)
	private String code;
	
	@Column(name = "price", nullable = false)
	private BigDecimal price;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "state", nullable = false)
	private ProductState productState;
	
	
	
}
