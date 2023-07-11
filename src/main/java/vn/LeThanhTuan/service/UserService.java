package vn.LeThanhTuan.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.springframework.web.multipart.MultipartFile;

import jakarta.mail.MessagingException;
import vn.LeThanhTuan.entity.dto.UserDto;

public interface UserService {

	UserDto findByEmail(String email);

	int sendOTPMail(String email) throws UnsupportedEncodingException, MessagingException;

	UserDto saveUser(UserDto userDto);

	UserDto getUserById(Integer id);

	UserDto updateUser(UserDto userDto, MultipartFile file) throws IOException;

}
