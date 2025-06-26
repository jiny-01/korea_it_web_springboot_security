package com.koreait.SpringSecurityStudy.mapper;

import com.koreait.SpringSecurityStudy.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserMapper {

    int addUser(User user);

    Optional<User> getUserByUserId(Integer userId);

    Optional<User> getUserByUsername(String username);

    //사용자 정보 수정 - 이메일, 비밀번호
    int updateEmail(User user);

    //비밀번호 수정
    int updatePassword(Integer userId, String password);

}
