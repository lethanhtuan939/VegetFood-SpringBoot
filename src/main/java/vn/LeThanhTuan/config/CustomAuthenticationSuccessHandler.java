package vn.LeThanhTuan.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.LeThanhTuan.entity.User;
import vn.LeThanhTuan.entity.dto.UserDto;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler{

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		 HttpSession session = request.getSession();
		 User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		 UserDto user = new UserDto();
		 user.setId(authUser.getId());
		 user.setEmail(authUser.getEmail());
		 user.setAddress(authUser.getAddress());
		 user.setActive(authUser.isActive());
		 user.setImage(authUser.getImage());
		 user.setPhoneNumber(authUser.getPhoneNumber());
		 
		 session.setAttribute("user", user);
		 
		 response.sendRedirect("vegetfood");
	}

	
}
