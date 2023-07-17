package vn.LeThanhTuan.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vn.LeThanhTuan.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {

	@Query("SELECT o FROM Order o WHERE o.user.id = ?1")
	Page<Order> findOrderByUserId(Integer id, Pageable pageable);
	
	@Query("SELECT o FROM Order o WHERE o.user.name like %?1% or o.address like %?1%")
	Page<Order> findAllOrderByKeyword(String keyword, Pageable pageable);

	@Query("SELECT COUNT(DISTINCT o.user.id) FROM Order o")
	long countCustomer();
	
	@Query("SELECT o FROM Order o WHERE o.user.name like %?1% or o.address like %?1%")
	List<Order> findAllByKeyword(String keyword);
}
