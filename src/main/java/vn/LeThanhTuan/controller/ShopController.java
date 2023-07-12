package vn.LeThanhTuan.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import vn.LeThanhTuan.entity.ShoppingCart;
import vn.LeThanhTuan.entity.dto.CategoryDto;
import vn.LeThanhTuan.entity.dto.ProductDto;
import vn.LeThanhTuan.entity.dto.UserDto;
import vn.LeThanhTuan.service.CartServive;
import vn.LeThanhTuan.service.CategoryService;
import vn.LeThanhTuan.service.ProductService;
import vn.LeThanhTuan.service.UserService;

@Controller
@RequestMapping("/vegetfood")
public class ShopController {
	
	@Autowired
	ProductService productService;
	
	@Autowired
	CategoryService categoryService;
	
	@Autowired
	CartServive cartServive;
	
	@Autowired
	UserService userService;

    @GetMapping()
    public String demo() {
    	
        return "index";
    }
    
    @GetMapping("/shop")
    public String shop(Model model, @RequestParam(value = "keyword", defaultValue = "") String keyword, 
    					@RequestParam(value = "p", defaultValue = "1") int pageIndex,
    					@RequestParam(value = "category", defaultValue = "0") Integer categoryId) {
    	
    	Page<ProductDto> page = productService.getAllProductByCategoryId(keyword, pageIndex, 8, categoryId);
    	List<ProductDto> products = page.getContent();
    	List<CategoryDto> categoryDtos = categoryService.getActiveCategory();
    	
    	int totalPages = page.getTotalPages();
    	
    	model.addAttribute("totalPages", totalPages);
		model.addAttribute("currentPage", pageIndex);
		model.addAttribute("products", products);
		model.addAttribute("categories", categoryDtos);
		model.addAttribute("keyword", keyword);
		model.addAttribute("selectedCategory", categoryId);
    	
    	return "shop";
    }
    
    @GetMapping("/shop/detail")
    public String getProductDetail(Model model, @RequestParam("id") Integer id, @RequestParam(value = "p", defaultValue = "1") int pageIndex) {
    	ProductDto product = productService.getProductById(id);
    	Integer categoryId = product.getCategory().getId();
 	
    	Page<ProductDto> page = productService.getAllProductByCategoryId("", pageIndex, 4, categoryId);
    	List<ProductDto> products = page.getContent();
    	int totalPages = page.getTotalPages();
    	
    	model.addAttribute("totalPages", totalPages);
    	model.addAttribute("relatedProducts", products);
    	model.addAttribute("product", product);
    	model.addAttribute("currentPage", pageIndex);
    	model.addAttribute("productId", id);
    	
    	return "product-detail";
    }
    
    @GetMapping("/add-to-cart")
	public String addToCart(@RequestParam("id") Integer id, @RequestParam("quantity") Integer quantity,
							Principal principal, HttpSession session, RedirectAttributes model) {
		String email = principal.getName();
		UserDto user = userService.findByEmail(email);
		
		Map<Integer, ShoppingCart> carts = cartServive.addToCart(user, session, id, quantity);
		if(carts.isEmpty()) {
			model.addFlashAttribute("error", "Không thể thêm sản phẩm vào giỏ hàng!");
		} else {
			model.addFlashAttribute("success", "Đã thêm sản phẩm vào giỏ hàng!");
		}
		
		return "redirect:/vegetfood/shop/detail?id=" + id;
	}
}
