package vn.LeThanhTuan.service.impl;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import vn.LeThanhTuan.entity.Order;
import vn.LeThanhTuan.entity.OrderDetail;
import vn.LeThanhTuan.entity.Product;
import vn.LeThanhTuan.entity.ShoppingCart;
import vn.LeThanhTuan.entity.User;
import vn.LeThanhTuan.entity.dto.UserDto;
import vn.LeThanhTuan.repository.OrderDetailRepository;
import vn.LeThanhTuan.repository.OrderRepository;
import vn.LeThanhTuan.repository.UserRepository;
import vn.LeThanhTuan.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	OrderDetailRepository orderDetailRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	JavaMailSender mailSender;

	@Value("${spring.mail.username}")
	private String fromEmail;

	@Override
	public int getTotalPrice(HttpSession session, UserDto userDto) {
		String cartKey = "cart_" + userDto.getId();
		Map<Integer, ShoppingCart> carts = (Map<Integer, ShoppingCart>) session.getAttribute(cartKey);

		int totalPrice = 0;
		Set<Integer> set = carts.keySet();

		for (Integer item : set) {
			totalPrice += carts.get(item).getProduct().getPrice() * carts.get(item).getQuantity();
		}

		return totalPrice;
	}

	@Override
	public void saveOrder(HttpSession session, UserDto userDto, String address, String phoneNumber, String payMethod, String status)
			throws MessagingException, IOException {
		String cartKey = "cart_" + userDto.getId();
		Map<Integer, ShoppingCart> carts = (Map<Integer, ShoppingCart>) session.getAttribute(cartKey);
		int totalPrice = getTotalPrice(session, userDto);

		Order order = new Order();
		order.setOrderDate(new Date());
		order.setTotalPrice(totalPrice);
		order.setShippingFee(0);
		order.setStatus(status);
		order.setNotes("");
		order.setAddress(address);
		order.setPhoneNumber(phoneNumber);

		User user = userRepository.findById(userDto.getId()).orElse(null);
		order.setUser(user);

		orderRepository.save(order); 

		List<OrderDetail> orderDetails = new ArrayList<>();

		for (Map.Entry<Integer, ShoppingCart> entry : carts.entrySet()) {
			ShoppingCart cart = entry.getValue();

			Product product = cart.getProduct();
			if (product == null) {
				continue;
			}

			OrderDetail detail = new OrderDetail();
			detail.setOrder(order);
			detail.setQuantity(cart.getQuantity());
			detail.setProduct(product);
			detail.setUnitPrice(product.getPrice());

			orderDetails.add(detail);
		}

		order.setOrderDetails(orderDetails);
		orderRepository.save(order);

		session.setAttribute(cartKey, new HashMap<>());
		session.setAttribute("totalItem", 0);

		sendEmail(userDto.getEmail(), carts, userDto.getAddress(), totalPrice, payMethod);
	}
	
	@Override
	public Page<Order> getAllOrder(int pageNumber, String keyword, int amountPage) {
		Sort sort = Sort.by("id");
		Pageable pageable = PageRequest.of(pageNumber-1, amountPage, sort);
		Page<Order> orders;
		if(keyword.trim().isEmpty()) {
			orders = orderRepository.findAll(pageable);
		} else {
			orders = orderRepository.findAllOrderByKeyword(keyword, pageable);
		}
		
		return orders;
	}
	
	@Override
	public Page<Order> getOrderByUser(Integer id, int pageNumber) {
		Sort sort = Sort.by("id");
		Pageable pageable = PageRequest.of(pageNumber-1, 5, sort);
		Page<Order> orders = orderRepository.findOrderByUserId(id, pageable);
		
		return orders;
	}
	
	@Override
	public List<OrderDetail> getOrderDetailByOrderId(Integer id) {
		List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(id);
		
		return orderDetails;
	}
	
	@Override
	public Order changeStatusOrder(Integer id, String status) {
		Order order = orderRepository.findById(id).orElse(null);
		order.setStatus(status);
		
		return orderRepository.save(order);
	}

	public void sendEmail(String toEmail, Map<Integer, ShoppingCart> cartItems, String address, int totalPrice, String payMethod)
	        throws MessagingException, IOException {
	    String toAddress = toEmail;
	    String senderName = "VegetFood";
	    String subject = "Đặt hàng thành công";

	    MimeMessage message = mailSender.createMimeMessage();
	    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

	    helper.setFrom(fromEmail, senderName);
	    helper.setTo(toAddress);
	    helper.setSubject(subject);

	    StringBuilder htmlBody = new StringBuilder();
	    htmlBody.append("<html><head>");
	    htmlBody.append("<style>");
	    htmlBody.append("body {");
	    htmlBody.append("  font-family: Arial, sans-serif;");
	    htmlBody.append("}");
	    htmlBody.append("h2 {");
	    htmlBody.append("  color: #333333;");
	    htmlBody.append("}");
	    htmlBody.append("table {");
	    htmlBody.append("  width: 100%;");
	    htmlBody.append("  border-collapse: collapse;");
	    htmlBody.append("  margin-bottom: 20px;");
	    htmlBody.append("}");
	    htmlBody.append("th, td {");
	    htmlBody.append("  padding: 8px;");
	    htmlBody.append("  text-align: left;");
	    htmlBody.append("  border-bottom: 1px solid #ddd;");
	    htmlBody.append("}");
	    htmlBody.append("th {");
	    htmlBody.append("  background-color: rgb(130, 174, 70);");
	    htmlBody.append("  color: #fff;");
	    htmlBody.append("}");
	    htmlBody.append("h3 {");
	    htmlBody.append("  color: #555555;");
	    htmlBody.append("}");
	    htmlBody.append("p {");
	    htmlBody.append("  margin: 0;");
	    htmlBody.append("  font-size: 16px;");
	    htmlBody.append("}");
	    htmlBody.append("</style>");
	    htmlBody.append("</head><body>");
	    htmlBody.append("<h2>Chi tiết đơn hàng:</h2>");
	    htmlBody.append("<table>");
	    htmlBody.append("<tr><th>Sản phẩm</th><th>Số lượng</th><th>Đơn giá</th></tr>");

	    for (Map.Entry<Integer, ShoppingCart> entry : cartItems.entrySet()) {
	        ShoppingCart cartItem = entry.getValue();
	        
	        htmlBody.append("<tr>");
	        htmlBody.append("<td>").append(cartItem.getProduct().getName()).append("</td>");
	        htmlBody.append("<td>").append(cartItem.getQuantity()).append("</td>");
	        htmlBody.append("<td>").append(cartItem.getProduct().getPrice()).append("</td>");
	        htmlBody.append("</tr>");
	    }

	    htmlBody.append("</table>");
	    
	    htmlBody.append("<h3>Tổng cộng:</h3>");
	    htmlBody.append("<p>").append(totalPrice).append("</p>");

	    htmlBody.append("<h3>Phương thức thanh toán:</h3>");
	    htmlBody.append("<p>").append(payMethod).append("</p>");


	    htmlBody.append("<h3>Ngày đặt hàng:</h3>");
	    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	    String formattedDate = dateFormat.format(new Date());
	    htmlBody.append("<p>").append(formattedDate).append("</p>");

	    htmlBody.append("<h3>Địa chỉ:</h3>");
	    htmlBody.append("<p>").append(address).append("</p>");

	    htmlBody.append("</body></html>");

	    message.setContent(htmlBody.toString(), "text/html; charset=UTF-8");

	    mailSender.send(message);
	}

}
