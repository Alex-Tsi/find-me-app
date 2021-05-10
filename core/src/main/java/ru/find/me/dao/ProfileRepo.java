package ru.find.me.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.find.me.model.Profile;

public interface ProfileRepo extends JpaRepository<Profile, Long> {
}
