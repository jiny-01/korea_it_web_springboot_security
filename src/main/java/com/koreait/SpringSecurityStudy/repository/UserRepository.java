package com.koreait.SpringSecurityStudy.repository;

import com.koreait.SpringSecurityStudy.entity.User;
import com.koreait.SpringSecurityStudy.mapper.UserMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Data
public class UserRepository {

    @Autowired
    private UserMapper userMapper;

    //회원가입 - 사용자 추가
    public Optional<User> addUser(User user) {   //원래 user -> username, pw, email 만 있음
        try {
            userMapper.addUser(user);           //xml에서 generatekeys 옵션해줬으므로 바로 set -> userId 넣어서 리턴해줌
        } catch (DuplicateKeyException e) {    //유저 아이디 중복
            return Optional.empty();           //없음 - 빈 껍데기 줌
        }
        return Optional.of(user);
    }

    //유저 아이디 일치 확인
    public Optional<User> getUserByUserId(Integer userId) {
        return userMapper.getUserByUserId(userId);
    }

    //유저 네임 있는지 확인
    public Optional<User> getUserByUsername(String username) {
        return userMapper.getUserByUsername(username);
    }

    //유저 이메일 수정
    public int updateEmail(User user) {
        return userMapper.updateEmail(user);
    }

    //유저 비밀번호 수정
    public int updatePassword(Integer userId, String password) {
        return userMapper.updatePassword(userId, password);
    }


}
