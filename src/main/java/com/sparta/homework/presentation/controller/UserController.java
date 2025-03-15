package com.sparta.homework.presentation.controller;

import com.sparta.homework.domain.model.UserRole;
import com.sparta.homework.dto.LoginReq;
import com.sparta.homework.dto.LoginRes;
import com.sparta.homework.dto.UserCreateRequest;
import com.sparta.homework.dto.UserResponse;
import com.sparta.homework.presentation.response.ErrorResponse;
import com.sparta.homework.presentation.response.SuccessResponse;
import com.sparta.homework.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "사용자 관리", description = "사용자 관리 API")
public class UserController {

    private final UserService userService;

    @Operation(summary = "일반 사용자 회원가입")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "회원가입 성공"),
        @ApiResponse(responseCode = "409", description = "이미 등록된 사용자",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/signup")
    public SuccessResponse<UserResponse> createUser(@RequestBody UserCreateRequest request) {
        UserResponse res = userService.signUp(request);
        return SuccessResponse.success(res);
    }

    @Operation(summary = "로그인")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그인 성공"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "비밀번호가 일치하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReq request) {
        LoginRes res = userService.login(request);

        return ResponseEntity.ok()
            .header("Authorization", "Bearer " + res.token())
            .body(res);
    }

    @Operation(summary = "사용자 역할 변경",
        description = "관리자 권한으로만 접근 가능한 사용자 역할 변경 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "역할 변경 성공"),
        @ApiResponse(responseCode = "403", description = "권한 없음 (ROLE_ADMIN 권한 필요)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })

    @SecurityRequirement(name = "bearerAuth")
    @Secured("ROLE_ADMIN")
    @PatchMapping("/admin/users/{userId}/roles")
    public SuccessResponse<UserResponse> updateUserRole(
        @Parameter(description = "사용자 ID", required = true) @PathVariable Long userId,
        @Parameter(description = "변경할 역할", required = true, schema = @Schema(implementation = UserRole.class))
        @RequestBody UserRole userRole) {

        UserResponse res = userService.updateUserRole(userId, userRole);
        return SuccessResponse.success(res);
    }
}