package com.sparta.homework.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {


    private Long id;

    private String userName;

    private String nickname;

    private String password;

    private UserRole Role;

    public static User createUser(String userName, String nickname, String password, UserRole role) {
        return User.builder()
            .userName(userName)
            .nickname(nickname)
            .password(password)
            .Role(role).build();
    }
    public void updateRole(UserRole role) {
        this.Role = role;
    }


}
