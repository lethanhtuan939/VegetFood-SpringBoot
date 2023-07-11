package vn.LeThanhTuan.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.LeThanhTuan.entity.dto.CategoryDto;
import vn.LeThanhTuan.service.CategoryService;
@Controller
@RequestMapping("/vegetfood/admin/category")
public class CategoryController {
	
	@Autowired
	CategoryService categoryService;
	
	@GetMapping
	public String getAllCategory(Model model, @RequestParam(value = "keyword", defaultValue = "") String keyword) {
		System.out.println(keyword);
		List<CategoryDto> categoryDtos = categoryService.getAllCategories(keyword);
		
		model.addAttribute("categories", categoryDtos);
		model.addAttribute("category", new CategoryDto());
		model.addAttribute("keyword", keyword);
		
		return "categories";
	}
	
	@PostMapping
	public String createCategory(@ModelAttribute CategoryDto categoryDto, RedirectAttributes model) {
		
		CategoryDto savedCategoryDto = categoryService.saveCategory(categoryDto);
		
		if(savedCategoryDto == null) {
			model.addFlashAttribute("error", "Thêm không thành công!");
		} else {
			model.addFlashAttribute("success", "Thêm thành công!");
		}
		
		return "redirect:category";
	}
	
	@PostMapping("/update")
	public String abledCategory(@ModelAttribute CategoryDto categoryDto, RedirectAttributes model) {
		
		CategoryDto category = categoryService.updateCategory(categoryDto, categoryDto.getId());
		
		if(category == null) {
			model.addFlashAttribute("error", "Chỉnh sửa không thành công!");
		} else {
			model.addFlashAttribute("success", "Chính sửa thành công!");
		}
		
		return "redirect:/vegetfood/admin/category";
	}

}
