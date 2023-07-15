package vn.LeThanhTuan.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecutiryConfig {
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	UserDetailsService userDetailsService() {
		return new CustomUserDetailsService();
	}
	
	@Bean
	public CustomAuthenticationSuccessHandler authenticationSuccessHandler() {
	    return new CustomAuthenticationSuccessHandler();
	}
	
	@Autowired
	CustomLogoutHandler logoutHandler;
	
	@Bean
	public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
		return new OAuth2AuthenticationSuccessHandler();
	}
	
	@Autowired
    CustomOAuth2UserService oauth2UserService;
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http
			.csrf(c -> c.disable())
			.authorizeHttpRequests(auth -> auth
					.requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
					.requestMatchers("/vegetfood/auth/**").permitAll()
					.requestMatchers("/vegetfood/add-to-cart/**").authenticated()
					.requestMatchers("/vegetfood/user/**").authenticated()
					.requestMatchers("/vegetfood/admin/**").hasAuthority("ADMIN")
					.requestMatchers("/vegetfood/checkout/**").authenticated()
					.requestMatchers(HttpMethod.POST).authenticated()
					.anyRequest().permitAll()
			)
			.formLogin(form -> form
					.loginPage("/vegetfood/auth/login")
					.loginProcessingUrl("/login")
					.defaultSuccessUrl("/vegetfood")
					.successHandler(authenticationSuccessHandler())
					.failureUrl("/vegetfood/auth/login?error=true")
					.usernameParameter("email")
					.passwordParameter("password")
			)
			.oauth2Login(oauth2 -> oauth2
					.loginPage("/vegetfood/auth/login")
	                .userInfoEndpoint(user -> user
	                		.userService(oauth2UserService)
	                )
	                .successHandler(oAuth2AuthenticationSuccessHandler())
	         )
			.rememberMe(remember -> remember
					.tokenValiditySeconds(30*24*60*60) // 30 days
					.userDetailsService(userDetailsService())
			)
			.logout(log -> log
					.logoutRequestMatcher(new AntPathRequestMatcher("/vegetfood/logout"))
					.addLogoutHandler(logoutHandler)
					.logoutSuccessUrl("/vegetfood")
					.invalidateHttpSession(true)
			);
		
		return http.build();
	}
	
	public void configureGlobal(AuthenticationManagerBuilder auth, CustomAuthenticationSuccessHandler authenticationSuccessHandler) throws Exception {
	    auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
	}

}
