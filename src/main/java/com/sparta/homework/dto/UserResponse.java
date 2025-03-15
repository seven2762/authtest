package com.sparta.homework.dto;

import com.sparta.homework.domain.model.UserRole;



public record UserResponse(String userName, String nickName, RoleResponse roles) {


    public record RoleResponse(UserRole role) {
    }

    public static UserResponse from(String userName, String nickName, UserRole role) {
        return new UserResponse(userName, nickName, new RoleResponse(role));
    }
}
