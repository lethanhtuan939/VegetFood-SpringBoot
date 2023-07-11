package vn.LeThanhTuan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vn.LeThanhTuan.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

	@Query("SELECT c FROM Category c WHERE c.id like %?1% or c.name like %?1%")
	List<Category> findByKeyword(String keyword);
	
	@Query("SELECT c FROM Category c WHERE c.id = ?1 and c.active = true")
	Optional<Category> isActiveCategory(Integer id);
	
	@Query("SELECT c FROM Category c WHERE c.active = true")
	List<Category> findActiveCategory();
}
