package ru.find.me;

import ru.find.me.model.Publication;

import java.util.List;

public interface PublicationService {

    List<Publication> findAll();

    Publication findById(long id);

    List<Publication> findByTags(String tag);

    void updatePublication(String text);

    void deletePublication(long id);

    void save(Publication publication);
}
