package vn.LeThanhTuan.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import vn.LeThanhTuan.entity.Product;
import vn.LeThanhTuan.entity.ShoppingCart;
import vn.LeThanhTuan.entity.dto.UserDto;
import vn.LeThanhTuan.repository.ProductRepository;
import vn.LeThanhTuan.service.CartServive;

@Service
public class CartServiceImpl implements CartServive {
	
	@Autowired
	ProductRepository productRepository;
	
	@Override
	public Map<Integer, ShoppingCart> addToCart(UserDto user, HttpSession session, Integer productId, Integer quantity) {
		String cartKey = "cart_" + user.getId();
		Map<Integer, ShoppingCart> carts = (Map<Integer, ShoppingCart>) session.getAttribute(cartKey);
		
		if(carts == null) {
			carts = new HashMap<>();
		}
		
		Product product = productRepository.findById(productId).get();
		
		if(product != null && product.isActive()) {
			ShoppingCart cartItem = carts.get(productId);
			if(cartItem != null) {
				cartItem.setQuantity(cartItem.getQuantity() + quantity);
			} else {
				carts.put(productId, new ShoppingCart(product, quantity));
			}
		}
		
		int totalItem = carts.size();
		session.setAttribute("totalItem", totalItem);
		session.setAttribute(cartKey, carts);
		
		return carts;
	}
	
	@Override
	public Map<Integer, ShoppingCart> removeItem(Integer productId, UserDto user, HttpSession session) {
		String cartKey = "cart_" + user.getId();
		Map<Integer, ShoppingCart> carts = (Map<Integer, ShoppingCart>) session.getAttribute(cartKey);
		carts.remove(productId);
		
		int totalItem = carts.size();
		session.setAttribute("totalItem", totalItem);
		session.setAttribute(cartKey, carts);
		
		return carts;
	}
	
	@Override
	public boolean updateCart(Integer productId, int quantity, UserDto user, HttpSession session) {
	    try {
	        String cartKey = "cart_" + user.getId();
	        Map<Integer, ShoppingCart> carts = (Map<Integer, ShoppingCart>) session.getAttribute(cartKey);

	       carts.get(productId).setQuantity(quantity);
	       return true;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}


}
