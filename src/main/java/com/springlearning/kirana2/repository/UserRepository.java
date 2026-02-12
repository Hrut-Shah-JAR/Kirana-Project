package com.springlearning.kirana2.repository;

import com.springlearning.kirana2.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUserName(String userName);

    boolean existsByUserName(String userName);
}
