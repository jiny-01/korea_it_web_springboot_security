package com.koreait.SpringSecurityStudy.repository;

import com.koreait.SpringSecurityStudy.entity.UserRole;
import com.koreait.SpringSecurityStudy.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRoleRepository {

    @Autowired
    private UserRoleMapper userRoleMapper;

    public Optional<UserRole> addUserRole(UserRole userRole) {
        return userRoleMapper.insert(userRole) <1             //0이면 안 들어간 거
                ? Optional.empty() : Optional.of(userRole);   //0 이 아니면 userRole
    }



    public Optional<UserRole> getUserRoleByUserIdAndRoleId(Integer userId, Integer roleId) {
        return userRoleMapper.getUserRoleByUserIdAndRoleId(userId, roleId);
    }

    public int updateRoleId(Integer userId, Integer userRoleId) {
        return userRoleMapper.updateRoleId(userId, userRoleId);
    }
}
