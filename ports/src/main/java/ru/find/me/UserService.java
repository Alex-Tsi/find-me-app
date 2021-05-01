package ru.find.me;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.find.me.model.User;

public interface UserService extends UserDetailsService {

    User findByUsername(String name);

    void save(User user);
}
