package com.korit.security_study2.mapper;

import com.korit.security_study2.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserMapper {
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserByUserId(Integer userId);
    Optional<User> getUserByEmail(String email);
    int modifyPassword(User user);
    void addUser(User user);
}
