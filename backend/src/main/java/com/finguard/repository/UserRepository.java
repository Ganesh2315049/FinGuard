package com.finguard.repository;
import com.finguard.entity.User;
import com.finguard.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface UserRepository extends JpaRepository<User, UUID> {
	Optional<User> findByEmailIgnoreCase(String email);
	boolean existsByEmailIgnoreCase(String email);
	long countByRole(UserRole role);
}
