package ru.find.me.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.find.me.model.Publication;

public interface PublicationRepo extends JpaRepository<Publication, Long> {

//    @Query(value = "SELECT m FROM Publication m WHERE m.tag = ?1")
//    List<Publication> findByTag(String tag);
}
