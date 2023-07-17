package vn.LeThanhTuan.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.LeThanhTuan.entity.Role;
import vn.LeThanhTuan.entity.dto.UserDto;
import vn.LeThanhTuan.service.UserService;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	
	@Autowired
	UserService userService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
	        Authentication authentication) throws IOException, ServletException {
		
		HttpSession session = request.getSession();
		OAuth2AuthenticationToken oauthUser = (OAuth2AuthenticationToken) authentication;
		
		String email = oauthUser.getPrincipal().getAttributes().get("email").toString();
		UserDto userDto = userService.findByEmail(email);
		String password = "123456";
		if(userDto != null) {
		} else {
			UserDto user = new UserDto();
			String name = oauthUser.getPrincipal().getAttributes().get("name").toString();

	        user.setName(name);
	        user.setEmail(email);
	        user.setPassword(password);

	        userDto = userService.saveUser(user);

		}
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
	    for(Role role : userDto.getRoles()) {
	    	authorities.add(new SimpleGrantedAuthority(role.getName()));
	    }
		Authentication auth = new UsernamePasswordAuthenticationToken(email, password, authorities);
		SecurityContextHolder.getContext().setAuthentication(auth);
		session.setAttribute("user", userDto);
		boolean redirectAdmin = false;
		for (Role role : userDto.getRoles()) {
		    if (role.getId() == 2) {
		        redirectAdmin = true;
		        break;
		    }
		}

		try {
		    if (redirectAdmin) {
		        response.sendRedirect("/vegetfood/admin");
		    } else {
		        response.sendRedirect("/vegetfood");
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		}

	}


}
