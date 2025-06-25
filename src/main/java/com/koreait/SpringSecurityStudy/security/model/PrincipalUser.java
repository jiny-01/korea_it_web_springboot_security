package com.koreait.SpringSecurityStudy.security.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.koreait.SpringSecurityStudy.entity.UserRole;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

// UserDetails 서비스에서 이걸 가져옴, 유저 정보를 Principal 객체로 만듦
@Data
@Builder
public class PrincipalUser implements UserDetails {
    private Integer userId;
    private String username;
    @JsonIgnore     //패스워드는 프론트에 띄우면 안됨 => JsonIgnore
    private String password;
    private String email;

    private List<UserRole> userRoles;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userRoles.stream()
                .map(userRole -> new SimpleGrantedAuthority(userRole.getRole().getRoleName()))
                .collect(Collectors.toList());
    }
        //Granted~ 를 상속받은 ? 어떤 Collection
//        return List.of();
//        return userRoles.stream().map(
//                userRole -> new SimpleGrantedAuthority
//                        (userRole.getRole().getRoleName())).collect(Collectors.toList());
//        ;
        //컬렉션 안에 있는 요소마다 어떤 것을 적용하는 것
    }

