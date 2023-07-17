package vn.LeThanhTuan.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.data.domain.Page;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import vn.LeThanhTuan.entity.Order;
import vn.LeThanhTuan.entity.OrderDetail;
import vn.LeThanhTuan.entity.dto.UserDto;

public interface OrderService {

	int getTotalPrice(HttpSession session, UserDto userDto);

	void saveOrder(HttpSession session, UserDto userDto, String address, String phoneNumber, String payMethod, String status, String note) throws UnsupportedEncodingException, MessagingException, IOException;

	Page<Order> getOrderByUser(Integer id, int pageIndex);

	List<OrderDetail> getOrderDetailByOrderId(Integer id);

	Order changeStatusOrder(Integer id, String status);

	Page<Order> getAllOrder(int pageNumber, String keyword, int amountPage);

	long count();

	long countCustomer();

	List<Order> getListOrder(String keyword);

}
