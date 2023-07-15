package vn.LeThanhTuan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vn.LeThanhTuan.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
	
	@Query("SELECT p FROM Product p WHERE p.active = true AND (p.id LIKE %?1% OR p.name LIKE %?1% OR p.description LIKE %?1% OR p.price like %?1% OR p.category.name LIKE %?1%)")
	Page<Product> findAllActiveProduct(String keyword, Pageable pageable);
	
	@Query("SELECT p FROM Product p WHERE p.category.id = ?1 AND p.active = true")
	Page<Product> findAllActiveProduct(Integer id, Pageable pageable);
	
	@Query("SELECT p FROM Product p WHERE p.active = true")
	Page<Product> findAllActiveProduct(Pageable pageable);
	
	@Query("SELECT p FROM Product p WHERE p.active = true AND p.id = ?1 AND p.category.active = true")
	Optional<Product> findActiveProduct(Integer id);
	
	List<Product> findTop8ByOrderByIdDesc();
}
