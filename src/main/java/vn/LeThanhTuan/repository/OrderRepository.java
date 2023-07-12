package vn.LeThanhTuan.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.LeThanhTuan.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {

}
