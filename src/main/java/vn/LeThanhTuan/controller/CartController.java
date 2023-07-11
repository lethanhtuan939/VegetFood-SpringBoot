package vn.LeThanhTuan.controller;

import java.security.Principal;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import vn.LeThanhTuan.entity.ShoppingCart;
import vn.LeThanhTuan.entity.dto.UserDto;
import vn.LeThanhTuan.service.CartServive;
import vn.LeThanhTuan.service.UserService;

@Controller
@RequestMapping("/vegetfood")
public class CartController {
	
	@Autowired
	CartServive cartServive;
	
	@Autowired
	UserService userService;
	
	@GetMapping("/cart")
	public String getCart(HttpSession session, Principal principal, Model model) {
		String email = principal.getName();
		UserDto user = userService.findByEmail(email);
		String cartKey = "cart_" + user.getId();
		Map<Integer, ShoppingCart> carts = (Map<Integer, ShoppingCart>) session.getAttribute(cartKey);
		
		if(carts.isEmpty()) {
			model.addAttribute("empty", "Bạn chưa có sản phẩm nào trong giỏ hàng!");
		} else {
			double totalPrice = 0;
			Set<Integer> set = carts.keySet();
			
			for (Integer item : set) {
				totalPrice += carts.get(item).getProduct().getPrice() * carts.get(item).getQuantity();
			}
			
			model.addAttribute("totalPrice", totalPrice);
			model.addAttribute("carts", carts);
		}
		
		return "cart";
	}
	
	@GetMapping("/cart/remove")
	public String removeItemCart(@RequestParam("id") Integer productId, RedirectAttributes attributes, Principal principal, HttpSession session) {
		String email = principal.getName();
		UserDto user = userService.findByEmail(email);
		
		Map<Integer, ShoppingCart> carts = cartServive.removeItem(productId, user, session);
		
		if(!carts.isEmpty()) {
			attributes.addFlashAttribute("success", "Đã xóa sản phẩm ra khỏi giỏ hàng!");
		}
		
		return "redirect:/vegetfood/cart";
	}
	
	@GetMapping("/cart/update")
	public String updateCart(@RequestParam("id") Integer productId, @RequestParam("quantity") int quantity,
							RedirectAttributes attributes, HttpSession session, Principal principal) {
		String email = principal.getName();
		UserDto user = userService.findByEmail(email);
		
		boolean isUpdated = cartServive.updateCart(productId, quantity, user, session);
		if(isUpdated) {
			attributes.addFlashAttribute("success", "Cập nhật thành công!");			
		} else {
			attributes.addFlashAttribute("error", "Cập nhật không thành công!");			
			
		}
		
		return "redirect:/vegetfood/cart";
	}

}
