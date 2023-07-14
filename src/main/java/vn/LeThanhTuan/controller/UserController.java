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

import jakarta.servlet.http.HttpSession;
import vn.LeThanhTuan.entity.Order;
import vn.LeThanhTuan.entity.OrderDetail;
import vn.LeThanhTuan.entity.dto.UserDto;
import vn.LeThanhTuan.service.OrderService;
import vn.LeThanhTuan.service.UserService;

@Controller
@RequestMapping("/vegetfood/user")
public class UserController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	OrderService orderService;
	
	@GetMapping
	public String getProfile(Model model, @RequestParam("id") Integer id, @RequestParam(value = "p", defaultValue = "1") int pageIndex) {
		
		UserDto user = userService.getUserById(id);
		
		if(user == null) {
			return "404";
		}
		
		model.addAttribute("user", user);
		
		Page<Order> page = orderService.getOrderByUser(id, pageIndex);
		if(page == null || page.isEmpty()) {
			model.addAttribute("empty", "Bạn chưa có đơn hàng nào!");
			return "profile";
		}
		List<Order> orders = page.getContent();
		
		int totalPages = page.getTotalPages();
		
		model.addAttribute("currentPage", pageIndex);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("orders", orders);
		
		return "profile";
	}
	
	@PostMapping
	public String updateUser(@ModelAttribute UserDto userDto,
							@RequestParam("file") MultipartFile file,
							RedirectAttributes attributes, HttpSession session) throws IOException {
		UserDto user = userService.updateUser(userDto, file);
		
		if(user == null) {
			attributes.addFlashAttribute("error", "Cập nhật không thành công!");
		} else {
			session.setAttribute("user", user);
			attributes.addFlashAttribute("success", "Cập nhật thành công!");
		}
		
		return "redirect:/vegetfood/user?id=" + userDto.getId();
	}
	
	@GetMapping("/order")
	public String getOrderById(@RequestParam("oid") Integer id, Model model,
								@RequestParam("id") Integer userId) {
		List<OrderDetail> details = orderService.getOrderDetailByOrderId(id);
		
		model.addAttribute("orders", details);
		model.addAttribute("userId", userId);
		
		return "order-user";
	}
	
	@PostMapping("/order")
	public String changeStatusOrder(@RequestParam("oid") Integer id, RedirectAttributes attributes,
									@RequestParam("id") Integer userId, @RequestParam(value = "p", defaultValue = "1") int pageIndex) {
		String status = "ĐÃ HỦY";
		Order order = orderService.changeStatusOrder(id, status);
		if(order == null) {
			attributes.addFlashAttribute("error", "Khong thể hủy đơn hàng!");
		} else {
			attributes.addFlashAttribute("success", "Đã hủy đơn hàng!");
		}
		
		return "redirect:/vegetfood/user?id=" + userId + "&p=" + pageIndex;
	}

}
