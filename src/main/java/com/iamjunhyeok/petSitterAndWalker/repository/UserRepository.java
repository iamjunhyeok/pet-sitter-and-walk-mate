package com.iamjunhyeok.petSitterAndWalker.repository;

import com.iamjunhyeok.petSitterAndWalker.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("select u.password from User u where u.id = :id")
    String getPasswordById(Long id);

    @Modifying
    @Query("update User u set u.password = :password where u.id = :id")
    int updatePasswordById(String password, Long id);
}
