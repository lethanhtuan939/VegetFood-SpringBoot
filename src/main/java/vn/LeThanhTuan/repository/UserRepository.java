package vn.LeThanhTuan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vn.LeThanhTuan.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByEmail(String email);
	
	@Query("SELECT u FROM User u WHERE u.name LIKE %?1% OR u.address LIKE %?1% OR u.email LIKE %?1% OR u.phoneNumber LIKE %?1%")
	Page<User> findAllByKeyword(String keyword, Pageable pageable);
	
	@Query("SELECT u FROM User u WHERE u.name LIKE %?1% OR u.address LIKE %?1% OR u.email LIKE %?1% OR u.phoneNumber LIKE %?1%")
	List<User> findAllByKeyword(String keyword);
}
