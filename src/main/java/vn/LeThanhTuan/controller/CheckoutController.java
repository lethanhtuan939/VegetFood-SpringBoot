package vn.LeThanhTuan.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import vn.LeThanhTuan.config.VNPayService;
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

	@Autowired
	VNPayService vnPayService;

	@GetMapping("/checkout")
	public String getCheckout(HttpSession session, Model model, Principal principal) {
		String email = principal.getName();
		UserDto userDto = userService.findByEmail(email);
		int totalPrice = orderService.getTotalPrice(session, userDto);

		model.addAttribute("totalPrice", totalPrice);

		return "checkout";
	}

	@PostMapping("/vnpay-payment")
	public String createPayment(@RequestParam("s") int totalPrice, HttpServletRequest request) {

		String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
		String vnPayUrl = vnPayService.createOrder(totalPrice, baseUrl);

		return "redirect:" + vnPayUrl;
	}

	@PostMapping("/checkout-success")
	public String successCheckout(@RequestParam("address") String address,
			@RequestParam("phoneNumber") String phoneNumber, Principal principal, HttpSession session, Model model)
			throws MessagingException, IOException {
		String email = principal.getName();
		UserDto userDto = userService.findByEmail(email);
		String payMethod = "Thanh toán khi nhận hàng";
		String status = "CHỜ XÁC NHẬN";

		orderService.saveOrder(session, userDto, address, phoneNumber, payMethod, status);
		
		model.addAttribute("paymentMethod", payMethod);

		return "successOrder";
	}

	@GetMapping("/vnpay-success")
	public String successCheckout(Principal principal, HttpSession session, Model model) throws MessagingException, IOException {
		String email = principal.getName();
		UserDto userDto = userService.findByEmail(email);
		String address = userDto.getAddress();
		String phoneNumber = userDto.getPhoneNumber();
		String payMethod = "Đã thanh toán với VNPay";
		String status = "ĐÃ THANH TOÁN";

		orderService.saveOrder(session, userDto, address, phoneNumber, payMethod, status);
		
		model.addAttribute("paymentMethod", payMethod);

		return "successOrder";
	}
}
