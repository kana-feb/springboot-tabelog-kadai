package com.example.nagoyameshi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    public User findByEmail(String email);

    public Page<User> findByNameLikeOrFuriganaLikeOrEmailLike(String nameKeyword, String furiganaKeyword,
            String emailKeyword, Pageable pageable);

    // public Page<User> findUsersByNameLikeOrFuriganaLike(String keyword, Pageable pageable) ;
    public long countByRole_Name(String roleName);
}