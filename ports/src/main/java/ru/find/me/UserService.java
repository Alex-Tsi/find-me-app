package ru.find.me;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.find.me.model.User;

import java.util.List;

public interface UserService extends UserDetailsService {

    User findByUsername(String name);

    void save(User user);

    User findById(Long id);

    List<User> findAll();
}
