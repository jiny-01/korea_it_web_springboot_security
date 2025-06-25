package com.koreait.SpringSecurityStudy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    private Integer userId;
    private String username;
    @JsonIgnore
    private String password;
    private String email;

    //엔티티의 정의로 따지면 위배되냐?
    //JPA 를 사용한다면 위배될 수 있다. - 완벽하게 1:1 매칭이 되어야함

    //하지만 Mybatis 에서는 엔티티가 DTO 개념을 함께 가지고 있다.
    //그럼 어차피 userId로 조인해서 조회하면 되는데 왜 굳이 멤버변수로 추가하냐?
    //객체 지향적 설계를 위함 => 객체를 참조할 수 있는 구조를 선호 (메소드 체이닝 등)
    private List<UserRole> userRoles;

}

/*권한 목록(role_tb) 과 유저 권한 목록을 따로 둔 이유
* 만약 유저당 하나의 권한만 가질 수 있을 떄
* User 는 1개의 role 만 가질 수 있음
* Role 은 N명의 사용자에게 부여될 수 있음 => 1:N 관계
* -> 하지만 이러면 관리자 + 일반사용자인 경우 동시에 두개의 권한을 가질 수 X
*
* User 는 여러 role 을 가질 수 있음
* Role 도 여러 Uesr 에게 부여될 수 있음
* -> N:M 관계
* -> 이러면 권한 관리가 복잡해지기 떄문에 권한 목록인 중간 테이블을 따로 분리해서 관리
*
* 한 사람이 여러가지 역할(role, M : N(다대다 관계) - 중간 테이블 생성 (user_role_tb) - 유저와 롤을 연결하는
* -> role_id 로 조인하기 위함
*
* */