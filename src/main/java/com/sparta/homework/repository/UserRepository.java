package com.sparta.homework.repository;

import com.sparta.homework.domain.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class UserRepository {

    private static ConcurrentHashMap<Long, User> users = new ConcurrentHashMap<>();
    private static long seq = 0;

    public User save(User user) {
        user.setId(++seq);
        users.put(user.getId(), user);
        log.info("User saved: {} , User.getId: {}", user.toString(), user.getId());
        return user;
    }

    public Optional<User> findById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    public Optional<User> findByUserName(String userName) {
        return findAll().stream().filter(user ->
            user.getUserName().equals(userName)).findFirst();
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
}
