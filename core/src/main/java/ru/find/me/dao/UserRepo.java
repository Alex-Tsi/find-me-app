package ru.find.me.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.find.me.model.User;

public interface UserRepo extends JpaRepository<User, Long> {

    User findUserByUsername(String name);
}
