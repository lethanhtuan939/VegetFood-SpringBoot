package vn.LeThanhTuan.service;

import java.util.Map;

import jakarta.servlet.http.HttpSession;
import vn.LeThanhTuan.entity.ShoppingCart;
import vn.LeThanhTuan.entity.dto.UserDto;

public interface CartServive {

	Map<Integer, ShoppingCart> addToCart(UserDto user, HttpSession session, Integer productId, Integer quantity);

	Map<Integer, ShoppingCart> removeItem(Integer productId, UserDto user, HttpSession session);

	boolean updateCart(Integer productId, int quantity, UserDto user, HttpSession session);

}
