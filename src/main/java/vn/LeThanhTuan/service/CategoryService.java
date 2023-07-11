package vn.LeThanhTuan.service;

import java.util.List;

import vn.LeThanhTuan.entity.dto.CategoryDto;

public interface CategoryService {

	List<CategoryDto> getAllCategories(String keyword);

	CategoryDto saveCategory(CategoryDto categoryDto);

	CategoryDto updateCategory(CategoryDto categoryDto, Integer id);

	CategoryDto getCategoryById(Integer id);

	List<CategoryDto> getActiveCategory();

}
