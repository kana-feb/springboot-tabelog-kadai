package com.example.nagoyameshi.repository;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.nagoyameshi.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    public User findByEmail(String email);

    public Page<User> findByNameLikeOrFuriganaLikeOrEmailLike(String nameKeyword, String furiganaKeyword,
            String emailKeyword, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.role.name IN :roleNames")
    Page<User> findByRoleNames(@Param("roleNames") Collection<String> roleNames, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.role.name IN :roleNames AND (u.name LIKE :nameKeyword OR u.furigana LIKE :furiganaKeyword OR u.email LIKE :emailKeyword)")
    Page<User> findByRoleNamesAndKeyword(@Param("roleNames") Collection<String> roleNames,
            @Param("nameKeyword") String nameKeyword, @Param("furiganaKeyword") String furiganaKeyword,
            @Param("emailKeyword") String emailKeyword, Pageable pageable);

    // public Page<User> findUsersByNameLikeOrFuriganaLike(String keyword, Pageable pageable) ;
    public Long countByRole_Name(String roleName);
}