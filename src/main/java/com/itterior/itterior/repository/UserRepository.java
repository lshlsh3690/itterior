package com.itterior.itterior.repository;

import com.itterior.itterior.entity.Product;
import com.itterior.itterior.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = {"roleList"})
    @Query("select u from User u where u.userName = :userName")
    User getWithRoles(String userName);

    @Query("select u from User u where u.userName= :username")
    Optional<User> selectOneByUserName( String username);

}
