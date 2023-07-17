package vn.LeThanhTuan.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import vn.LeThanhTuan.entity.dto.CategoryDto;
import vn.LeThanhTuan.entity.dto.ProductDto;
import vn.LeThanhTuan.entity.dto.UserDto;
import vn.LeThanhTuan.service.CategoryService;
import vn.LeThanhTuan.service.ProductService;
import vn.LeThanhTuan.util.AppConstrant;
import vn.LeThanhTuan.util.ExcelExportUtil;

@Controller
@RequestMapping("/vegetfood/admin/product")
public class ProductController {
	
	@Autowired
	ProductService productService;
	
	@Autowired
	CategoryService categoryService;
	
	@GetMapping
	public String getAllProduct(Model model, @RequestParam(value = "keyword", defaultValue = "") String keyword, 
								@RequestParam(value = "p", defaultValue = "1") int pageIndex,
	                            @RequestParam(value = "s", defaultValue = "5") int pageSize) {
		Page<ProductDto> page = productService.getAllProduct(keyword, pageIndex, pageSize);
		List<CategoryDto> categoryDtos = categoryService.getAllCategories("");
		
		int totalPages = page.getTotalPages();
		
		List<ProductDto> products = page.getContent();
		
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("currentPage", pageIndex);
		model.addAttribute("products", products);
		model.addAttribute("keyword", keyword);
		model.addAttribute("categories", categoryDtos);
		model.addAttribute("product", new ProductDto());
		model.addAttribute("pageSize", pageSize);
		
		return "products";
	}
	
	@GetMapping("/add")
	public String getProductAdd(Model model, @RequestParam(value = "p", defaultValue = "1") int pageIndex,
			@RequestParam(value = "s", defaultValue = "5") int pageSize) {
		List<CategoryDto> categoryDtos = categoryService.getAllCategories("");
		
		model.addAttribute("product", new ProductDto());
		model.addAttribute("categories", categoryDtos);
		model.addAttribute("currentPage", pageIndex);
		model.addAttribute("pageSize", pageSize);
		
		return "product-add";
	}
	
	@PostMapping("/add")
	public String createProduct(@RequestParam("file") MultipartFile file,
								@ModelAttribute ProductDto productDto,
	                            RedirectAttributes attributes,
	                            @RequestParam(value = "p", defaultValue = "1") int pageIndex,
	                            @RequestParam(value = "s", defaultValue = "5") int pageSize) throws IOException {
	    if (file.isEmpty()) {
	        attributes.addFlashAttribute("error", "Không có ảnh nào được chọn!");
	        return "redirect:/vegetfood/admin/product";
	    }
	    
	    ProductDto product = productService.createProduct(productDto, file);
	    
	    if (product == null) {
	        attributes.addFlashAttribute("error", "Thêm không thành công");
	    } else {
	        attributes.addFlashAttribute("success", "Thêm thành công!");
	    }

	    return "redirect:/vegetfood/admin/product?p=" + pageIndex + "&s=" + pageSize;
	}

	@PostMapping("/update")
	public String updateProduct(@RequestParam("file") MultipartFile file,
								@ModelAttribute ProductDto productDto,
	                            RedirectAttributes attributes,
	                            @RequestParam(value = "p", defaultValue = "1") int pageIndex) throws IOException {
		
	    ProductDto product = productService.updateProduct(productDto, file);
	    
	    if (product == null) {
	        attributes.addFlashAttribute("error", "Chỉnh sửa không thành công");
	    } else {
	        attributes.addFlashAttribute("success", "Chỉnh sửa thành công!");
	    }

	    return "redirect:/vegetfood/admin/product?p=" + pageIndex;
	}
	
	@GetMapping("/excel")
	public String exportToExcel(HttpServletResponse response,
			@RequestParam(value = "keyword", defaultValue = "") String keyword) throws IllegalArgumentException, IllegalAccessException, IOException {

		List<ProductDto> products = productService.getListProduct(keyword);
		
		ExcelExportUtil<ProductDto> ex = new ExcelExportUtil<>(products);
		String baseName = AppConstrant.EXPORT_PRODUCT;

		ex.export(response, baseName);
		
		return "redirect:/vegetfood/admin/product?keyword=" + keyword;
	}

}
