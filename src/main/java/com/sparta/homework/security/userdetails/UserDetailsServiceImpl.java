package com.sparta.homework.security.userdetails;

import com.sparta.homework.domain.model.User;
import com.sparta.homework.exception.CustomException;
import com.sparta.homework.exception.ErrorStatus;
import com.sparta.homework.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(userName)
            .orElseThrow(() -> new CustomException(ErrorStatus.UNEXPECTED_PRINCIPAL_TYPE));
        return new UserDetailsImpl(user);
    }
}