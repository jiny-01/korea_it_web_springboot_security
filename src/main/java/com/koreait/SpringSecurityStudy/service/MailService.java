package com.koreait.SpringSecurityStudy.service;

import com.koreait.SpringSecurityStudy.dto.ApiRespDto;
import com.koreait.SpringSecurityStudy.dto.SendMailReqDto;
import com.koreait.SpringSecurityStudy.entity.User;
import com.koreait.SpringSecurityStudy.repository.UserRepository;
import com.koreait.SpringSecurityStudy.security.jwt.JwtUtil;
import com.koreait.SpringSecurityStudy.security.model.PrincipalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MailService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JavaMailSender javaMailSender;
    //이메일 보냄
    public ApiRespDto<?> sendMail(SendMailReqDto sendMailReqDto, PrincipalUser principalUser) {
        //사용자는 jwt 토큰을 가진 상태 - principaluser 존재한다는 의미

        //입력한 이메일 주소와 principaluser 에 등록된 email 일치하는지
        if(!principalUser.getEmail().equals(sendMailReqDto.getEmail())) {
            return new ApiRespDto<>("failed", "이메일 불일치 - 잘못된 접근", null);
        }

        //해당 이메일이 회원정보에 있는지 확인
        Optional<User> optionalUser = userRepository.getUserByEmail(sendMailReqDto.getEmail());

        if(optionalUser.isEmpty()) {
            return new ApiRespDto<>("failed", "사용자 정보를 확인해주세요", null);
        }

        //이미 인증된 일반사용자 -> 보낼 필요 없음
        User user = optionalUser.get();    //유저 객체로 가져옴

        boolean hasTempRole = user.getUserRoles().stream()
                .anyMatch(userRole -> userRole.getRoleId() == 3);
        //있는지 없는지 판단 -> anymatch
        if (!hasTempRole) {
            return new ApiRespDto<>("failed", "인증이 필요한 계정이 아님 - 이미 일반사용자", null);
        }

        //링크에 토큰을 넣어놓을 것(서버로 돌아감) - 토큰을 다시 가져옴 - 회원정보 맞는지 확인

        String token = jwtUtil.generateMailVerifyToken(user.getUserId().toString());

        //사용자에게 보여질 이메일 만들기 -> SimpleMailMessage
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());   //수신자의 이메일
        message.setSubject("이메일 인증메일입니다");       //메일 제목 설정
        message.setText("링크를 클릭해 인증을 완료해주세요 : " +
                "http://localhost:8080/mail/verify?verifyToken= " + token);
        //우리 서버에 인증용 토큰을 같이 보냄
        //verifytoken 이 맞는지, 유저 ID, 유저 role 확인

        //메일 보내기 -> javaMailSender 가 보내줌
        javaMailSender.send(message);

        return new ApiRespDto<>("success", "인증메일이 전송되었습니다. 메일을 확인하세요", null);
    }
}
