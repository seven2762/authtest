package com.sparta.homework.presentation.controller;

import com.sparta.homework.domain.model.User;
import com.sparta.homework.dto.UserCreateRequest;
import com.sparta.homework.dto.UserResponse;
import com.sparta.homework.presentation.response.SuccessResponse;
import com.sparta.homework.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Profile("test")
public class TestController {

    private final UserService userService;

    @PostMapping("/signup")
    public SuccessResponse<UserResponse> signUpAdmin(@RequestBody UserCreateRequest request){
        UserResponse response = userService.signUpAdmin(request);
        return SuccessResponse.success(response);

    }
}
