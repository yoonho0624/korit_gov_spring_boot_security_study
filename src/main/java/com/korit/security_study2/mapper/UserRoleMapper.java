package com.korit.security_study2.mapper;

import com.korit.security_study2.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRoleMapper {
    void addUserRole(UserRole userRole);
    void updateUserRole(UserRole userRole);
}
