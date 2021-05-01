package ru.find.me;

import ru.find.me.model.Publication;

import java.util.List;

public interface PublicationService {

    List<Publication> findAll();

    Publication findById(int id);

    List<Publication> findByTag(String tag);

    void updatePublication(String text);

    void deletePublication(int id);

    void save(Publication message);
}
