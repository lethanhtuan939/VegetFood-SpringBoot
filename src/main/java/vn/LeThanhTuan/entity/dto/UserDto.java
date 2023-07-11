package vn.LeThanhTuan.entity.dto;

import java.io.Serializable;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
    
	@Size(min = 2, max = 10, message = "Tên phải từ 2 đến 10 kí tự!)")
    private String name;

	@Email(message = "Email không hợp lệ")
    private String email;

	@Size(min = 3, max = 10, message = "Mật khẩu phải 5 đến 10 kí tự!")
    private String password;
	
	private String image;
    
    private String address;
    
    private String phoneNumber;

    private boolean active;
    
}
