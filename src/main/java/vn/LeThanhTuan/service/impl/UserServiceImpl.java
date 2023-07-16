package vn.LeThanhTuan.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import vn.LeThanhTuan.entity.Role;
import vn.LeThanhTuan.entity.User;
import vn.LeThanhTuan.entity.dto.UserDto;
import vn.LeThanhTuan.repository.RoleRepository;
import vn.LeThanhTuan.repository.UserRepository;
import vn.LeThanhTuan.service.UserService;
import vn.LeThanhTuan.util.FileUploadUtil;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	ModelMapper modelMapper;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	JavaMailSender mailSender;
	
	String uploadDir = "src/main/resources/static/images";
	
	@Value("${spring.mail.username}")
	private String fromAddress;
	
	private UserDto toDto(User user) {
		return modelMapper.map(user, UserDto.class);
	}
	
	private User toUser(UserDto userDto) {
		return modelMapper.map(userDto, User.class);
	}

	@Override
	public UserDto findByEmail(String email) {
		Optional<User> user = userRepository.findByEmail(email);
		
		if(user.isEmpty()) {
			return null;
		}
		
		return toDto(user.get());
	}
	
	@Override
	public UserDto saveUser(UserDto userDto) {
		
		try {
			User user = toUser(userDto);
			user.setPassword(passwordEncoder.encode(userDto.getPassword()));
			Set<Role> roles = new HashSet<>();
			roles.add(roleRepository.findById(1).get());
			user.setRoles(roles);
			user.setActive(true);
			
			User savedUser = userRepository.save(user);
			
			return toDto(savedUser);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private int generateCode() {
		Random random = new Random();
        int min = 100_000;
        int max = 999_999;
        int code = random.nextInt(max - min + 1) + min;
	    
        return code;
	}

	@Override
	public int sendOTPMail(String email) throws UnsupportedEncodingException, MessagingException {
		String toAddress = email;
	    String senderName = "VegetFood";
	    String subject = "Đăng ký tài khoản";
	    String content = "Mã xác thực của bạn là: [[code]]";
	     
	    MimeMessage message = mailSender.createMimeMessage();
	    MimeMessageHelper helper = new MimeMessageHelper(message);
	    
	    int code = generateCode();
	    
	    helper.setFrom(fromAddress, senderName);
	    helper.setTo(toAddress);
	    helper.setSubject(subject);
	    
	    content = content.replace("[[code]]", String.valueOf(code));
	     
	    helper.setText(content);
	     
	    mailSender.send(message);
	    
	    return code;
	}
	
	@Override
	public UserDto getUserById(Integer id) {
		Optional<User> optional = userRepository.findById(id);
		
		if(optional.isEmpty()) {
			return null;
		}
		
		User user = optional.get();
		
		return toDto(user);
	}
	
	@Override
	public UserDto updateUser(UserDto userDto, MultipartFile file) throws IOException {
		
		Optional<User> optional = userRepository.findById(userDto.getId());
		
		if(optional.isEmpty()) {
			return null;
		}
		
		User user = optional.get();
		String fileName = "";
		
		if(!file.isEmpty()) {
			fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
			user.setImage(fileName);
			FileUploadUtil.saveFile(uploadDir, fileName, file);
		}
		user.setAddress(userDto.getAddress());
		user.setName(userDto.getName());
		user.setEmail(userDto.getEmail());
		user.setPhoneNumber(userDto.getPhoneNumber());
		
		User updatedUser = userRepository.save(user);
		
		return toDto(updatedUser);
	}
	
	@Override
	public UserDto changePassword(String password, UserDto userDto) {
		try {
			User user = toUser(userDto);
			user.setPassword(passwordEncoder.encode(password));
			
			return toDto(userRepository.save(user));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public boolean comparePassword(UserDto userDto, String password) {
		return passwordEncoder.matches(password, userDto.getPassword());
	}
}
