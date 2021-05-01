package ru.find.me.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.find.me.model.Publication;

import java.util.List;

public interface PublicationRepo extends JpaRepository<Publication, Integer> {

    @Query(value = "SELECT m FROM Publication m WHERE m.tag = ?1")
    List<Publication> findByTag(String tag);
}
