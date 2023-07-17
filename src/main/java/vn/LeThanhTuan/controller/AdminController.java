package vn.LeThanhTuan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import vn.LeThanhTuan.service.OrderService;
import vn.LeThanhTuan.service.ProductService;
import vn.LeThanhTuan.service.UserService;

@Controller
@RequestMapping("/vegetfood/admin")
public class AdminController {
	
	@Autowired
	ProductService productService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	UserService userService;
	
	@GetMapping
	public String getAdmin(Model model) {
		long totalProducts = productService.count();
		long totalOrders = orderService.count();
		long totalCustomers = orderService.countCustomer();
		long totalUsers = userService.count();
		
		model.addAttribute("totalProducts", totalProducts);
		model.addAttribute("totalOrders", totalOrders);
		model.addAttribute("totalCustomers", totalCustomers);
		model.addAttribute("totalUsers", totalUsers);
		return "dashboard";
	}

}
