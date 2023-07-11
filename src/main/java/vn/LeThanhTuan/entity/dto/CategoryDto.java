package vn.LeThanhTuan.entity.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class CategoryDto {
	
	private Integer id;
	
	private String name;
	
	private boolean active;
	
	private List<ProductDto> products = new ArrayList<>();

}
