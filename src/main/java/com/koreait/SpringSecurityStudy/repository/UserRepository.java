package com.koreait.SpringSecurityStudy.repository;

import com.koreait.SpringSecurityStudy.entity.User;
import com.koreait.SpringSecurityStudy.mapper.UserMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@Data
public class UserRepository {

    @Autowired
    private UserMapper userMapper;

    //회원가입 - 사용자 추가
    public int addUser(User user) {
        return userMapper.addUser(user);

    }
}
