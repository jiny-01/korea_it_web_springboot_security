package com.koreait.SpringSecurityStudy.mapper;

import com.koreait.SpringSecurityStudy.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserRoleMapper {

    //유저 권한
    int insert(UserRole userRole);

    //유저Id, 권한 ID 가져오기
    Optional<UserRole> getUserRoleByUserIdAndRoleId(Integer userId, Integer roleId);

    //변경
    int updateRoleId(Integer userId, Integer userRoleId);


    Optional<UserRole> getUserRoleByUserId(Integer userId);
}
