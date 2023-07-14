package vn.LeThanhTuan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vn.LeThanhTuan.entity.OrderDetail;
import vn.LeThanhTuan.entity.id.OrderDetailId;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailId> {

	@Query("SELECT o FROM OrderDetail o WHERE o.order.id = ?1")
	List<OrderDetail> findByOrderId(Integer id);
}
