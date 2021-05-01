package ru.find.me;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.find.me.dao.UserRepo;
import ru.find.me.model.User;

import javax.persistence.EntityManager;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final EntityManager manager;

    public UserServiceImpl(@Qualifier("userRepo") UserRepo userRepo,
                           @Qualifier("entityManagerFactory") EntityManager manager) {
        this.userRepo = userRepo;
        this.manager = manager;
    }

    @Override
    public User findByUsername(String name) {
        return userRepo.findUserByUsername(name);
    }

    @Override
    public void save(User user) {
        userRepo.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepo.findUserByUsername(s);
    }
}
