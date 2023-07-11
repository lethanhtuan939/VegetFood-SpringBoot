package vn.LeThanhTuan.entity.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	
	private String name;
	
	private String description;
	
	private double price;
	
	private String image;
	
	private boolean active;
	
	private CategoryDto category;
}
