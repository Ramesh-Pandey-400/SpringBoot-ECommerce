package com.application.firstapp.repository;

import com.application.firstapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByUserName(String username);

    boolean existsByUserName(String user1);

    boolean existsByEmail(String email);
}
