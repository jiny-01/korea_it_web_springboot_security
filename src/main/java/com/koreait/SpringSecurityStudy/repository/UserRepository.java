package com.koreait.SpringSecurityStudy.repository;

import com.koreait.SpringSecurityStudy.entity.User;
import com.koreait.SpringSecurityStudy.mapper.UserMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Data
public class UserRepository {

    @Autowired
    private UserMapper userMapper;

    //회원가입 - 사용자 추가
    public int addUser(User user) {
        return userMapper.addUser(user);

    }

    //유저 아이디 일치 확인
    public Optional<User> getUserByUserId(Integer userId) {
        return userMapper.getUserByUserId(userId);
    }

    //유저 네임 있는지 확인
    public Optional<User> getUserByUsername(String username) {
        return userMapper.getUserByUsername(username);
    }


}
