package vn.LeThanhTuan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import vn.LeThanhTuan.entity.dto.UserDto;
import vn.LeThanhTuan.service.UserService;

@Controller
@RequestMapping("/vegetfood/auth")
public class AuthController {

	@Autowired
	UserService userService;

	@GetMapping("/register")
	public String registerGet(Model model) {

		model.addAttribute("user", new UserDto());

		return "register";
	}

	@PostMapping("/register")
	public String registerPost(@Valid @ModelAttribute("user") UserDto userDto, 
								BindingResult result, Model model,
								@RequestParam("repeatPassword") String repeatPassword, 
								RedirectAttributes attributes,
								HttpSession session) {

		System.out.println(userDto);
		try {
			if (result.hasErrors()) {
				model.addAttribute("user", userDto);
				return "register";
			}

			String email = userDto.getEmail();
			UserDto isUserExist = userService.findByEmail(email);
			if (isUserExist != null) {
				model.addAttribute("user", userDto);
				model.addAttribute("emailError", "Email này đã được đăng ký trước đó!");

				return "register";
			}

			if (!userDto.getPassword().equals(repeatPassword)) {
				model.addAttribute("userDto", userDto);
				model.addAttribute("passwordError", "Mật khẩu nhập lại của bạn không chính xác!");

				return "register";
			}

			int otpCode = userService.sendOTPMail(email);
			
			attributes.addFlashAttribute("otpCode", otpCode);
			session.setAttribute("user", userDto);
			
			return "redirect:vertify";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "Máy chủ không phản hồi!");

			return "register";
		}

	}

	@GetMapping("/vertify")
	public String getVertify() {
		
		return "vertify";
	}

	@PostMapping("/vertify")
	public String postVertify(Model model, @RequestParam("otp") int otp,
								RedirectAttributes attributes,
								@ModelAttribute("otpCode") int otpCode,
								HttpSession session) {
		
		System.out.println("OTP: " + otp);
		System.out.println("OTP CODE: " + otpCode);
		
		UserDto userDto = (UserDto) session.getAttribute("user");
		System.out.println(userDto);
		if (otp != otpCode) {
			model.addAttribute("error", "Mã xác thực không chính xác!");
			model.addAttribute("otpCode", otpCode);

			return "vertify";
		}

		UserDto savedUser = userService.saveUser(userDto);
		
		if(savedUser == null) {
			attributes.addFlashAttribute("error", "Có lỗi xảy ra trong quá trình đăng ký!");

			return "redirect:register";
		}

		session.removeAttribute("user");
		attributes.addFlashAttribute("success", "Bạn đã đăng ký thành công, xin mời đăng nhập");
		return "redirect:login";
	}

	@GetMapping("/login")
	public String login() {
		
		return "login";
	}
}
