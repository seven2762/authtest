package com.sparta.homework.service;


import com.sparta.homework.domain.model.User;
import com.sparta.homework.domain.model.UserRole;
import com.sparta.homework.dto.LoginReq;
import com.sparta.homework.dto.LoginRes;
import com.sparta.homework.dto.UserCreateRequest;
import com.sparta.homework.dto.UserResponse;
import com.sparta.homework.exception.CustomException;
import com.sparta.homework.exception.ErrorStatus;
import com.sparta.homework.repository.UserRepository;
import com.sparta.homework.security.jwt.JwtUtil;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;

    public synchronized UserResponse signUp(UserCreateRequest request) {

        validationUser(request.userName());

        User user = User.createUser(
            request.userName(),
            request.nickName(),
            bCryptPasswordEncoder.encode(request.password()),
            UserRole.USER);

        User savedUser = userRepository.save(user);

        return UserResponse.from(
            savedUser.getUserName(),
            savedUser.getNickname(),
            savedUser.getRole());
    }

    public synchronized UserResponse signUpAdmin(UserCreateRequest request) {

        validationUser(request.userName());

        User user = User.createUser(
            request.userName(),
            request.nickName(),
            bCryptPasswordEncoder.encode(request.password()),
            UserRole.ADMIN
        );

        User savedUser = userRepository.save(user);

        return UserResponse.from(
            savedUser.getUserName(),
            savedUser.getNickname(),
            savedUser.getRole());
    }

    private void validationUser(String userName) {

        if (userName == null || userName.isBlank()) {
            throw new CustomException(ErrorStatus.BAD_REQUEST);
        }
        Optional<User> existingUser = userRepository.findByUserName(userName);
        if (existingUser.isPresent()) {
            throw new CustomException(ErrorStatus.USER_DUPLICATE);
        }
    }

    public LoginRes login(LoginReq req) {
        User user = userRepository.findByUserName(req.userName())
            .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));
        validatePassword(req.password(), user);
        String token = jwtUtil.createToken(user.getUserName(), user.getRole());

        return new LoginRes(token, user.getRole());
    }


    private void validatePassword(String password, User user) {
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(ErrorStatus.INVALID_LOGIN_INFO);
        }
    }

    public UserResponse updateUserRole(Long userId, UserRole userRole) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));
        user.updateRole(userRole);
        return UserResponse.from(user.getUserName(), user.getNickname(), user.getRole());
    }
}
