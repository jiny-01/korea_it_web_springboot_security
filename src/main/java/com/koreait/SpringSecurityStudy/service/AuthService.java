package com.koreait.SpringSecurityStudy.service;

import com.koreait.SpringSecurityStudy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
}
