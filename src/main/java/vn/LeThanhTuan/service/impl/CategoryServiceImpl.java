package vn.LeThanhTuan.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.LeThanhTuan.entity.Category;
import vn.LeThanhTuan.entity.dto.CategoryDto;
import vn.LeThanhTuan.repository.CategoryRepository;
import vn.LeThanhTuan.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {
	
	@Autowired
	CategoryRepository categoryRepository;
	
	@Autowired
	ModelMapper modelMapper;
	
	private CategoryDto toDto(Category category) {
		return modelMapper.map(category, CategoryDto.class);
	}
	
	private Category toCategory(CategoryDto categoryDto) {
		return modelMapper.map(categoryDto, Category.class);
	}
	
	@Override
	public CategoryDto saveCategory(CategoryDto categoryDto) {
		try {
			Category category = toCategory(categoryDto);
			
			category.setActive(true);
			
			Category savedCategory = categoryRepository.save(category);
			
			return toDto(savedCategory);
		} catch (Exception e) {
			e.printStackTrace();
			
			return null;
		}
	}
	
	@Override
	public List<CategoryDto> getAllCategories(String keyword) {
		List<Category> categories = new ArrayList<>();
		if(!keyword.isEmpty()) {			
			categories = categoryRepository.findByKeyword(keyword);
		} else {
			categories = categoryRepository.findAll();
		}
		
		return categories.stream().map(this::toDto).collect(Collectors.toList());
	}
	
	@Override
	public List<CategoryDto> getActiveCategory() {
		List<Category> categories = categoryRepository.findActiveCategory();
		
		return categories.stream().map(this::toDto).collect(Collectors.toList());
	}
	
	@Override
	public CategoryDto updateCategory(CategoryDto categoryDto, Integer id) {
		
		Optional<Category> optional = categoryRepository.findById(id);
		
		if(optional.isEmpty()) {
			return null;
		}
		
		Category category = optional.get();
		
		category.setName(categoryDto.getName());
		category.setActive(categoryDto.isActive());
		
		Category updatedCategory = categoryRepository.save(category);
		
		return toDto(updatedCategory);
	}
	
	
	@Override
	public CategoryDto getCategoryById(Integer id) {
		Optional<Category> category = categoryRepository.findById(id);
		
		if(category.isEmpty()) {
			return null;
		}
		
		return toDto(category.get());
	}

}
