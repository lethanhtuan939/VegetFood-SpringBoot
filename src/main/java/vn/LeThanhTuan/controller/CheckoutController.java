package vn.LeThanhTuan.controller;

import java.io.IOException;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.LeThanhTuan.entity.dto.UserDto;
import vn.LeThanhTuan.service.OrderService;
import vn.LeThanhTuan.service.UserService;

@Controller
@RequestMapping("/vegetfood")
public class CheckoutController {
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	UserService userService;
	
	@GetMapping("/checkout")
	public String getCheckout(HttpSession session, Model model, Principal principal) {
		String email = principal.getName();
		UserDto userDto = userService.findByEmail(email);
		double totalPrice = orderService.getTotalPrice(session, userDto);
		
		model.addAttribute("totalPrice", totalPrice);
		
		return "checkout";
	}
	
	@PostMapping("/checkout-success")
	public String successCheckout(@RequestParam("address") String address,
	                              @RequestParam("phoneNumber") String phoneNumber,
	                              Principal principal,
	                              HttpSession session) throws MessagingException, IOException {
	    String email = principal.getName();
	    UserDto userDto = userService.findByEmail(email);
	    
	    orderService.saveOrder(session, userDto, address, phoneNumber);
	    
	    return "successOrder";
	}


}
