package com.sparta.homework.dto;

import com.sparta.homework.domain.model.UserRole;

public record LoginRes (String token, UserRole role){

}
