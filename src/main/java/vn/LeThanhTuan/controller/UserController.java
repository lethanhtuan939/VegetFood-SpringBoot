package vn.LeThanhTuan.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
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
import vn.LeThanhTuan.entity.dto.UserDto;
import vn.LeThanhTuan.service.UserService;

@Controller
@RequestMapping("/vegetfood/user")
public class UserController {
	
	@Autowired
	UserService userService;
	
	@GetMapping
	public String getProfile(Model model, @RequestParam("id") Integer id) {
		
		UserDto user = userService.getUserById(id);
		
		if(user == null) {
			return "404";
		}
		
		model.addAttribute("user", user);
		
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

}
