package vn.LeThanhTuan.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import jakarta.mail.MessagingException;
import vn.LeThanhTuan.entity.dto.UserDto;

public interface UserService {

	UserDto findByEmail(String email);

	int sendOTPMail(String email) throws UnsupportedEncodingException, MessagingException;

	UserDto saveUser(UserDto userDto);

	UserDto getUserById(Integer id);

	UserDto updateUser(UserDto userDto, MultipartFile file) throws IOException;

	UserDto changePassword(String password, UserDto userDto);

	boolean comparePassword(UserDto userDto, String password);

	Page<UserDto> getAllUsers(String keyword, int pageNumber, int amountPage);

	long count();

	List<UserDto> getListUser();

	List<UserDto> getListUser(String keyword);

}
