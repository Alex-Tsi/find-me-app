package ru.find.me;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.find.me.dao.PublicationRepo;
import ru.find.me.model.Publication;

import javax.persistence.EntityManager;
import java.util.List;

@Service
public class PublicationServiceImpl implements PublicationService {

    private final PublicationRepo publicationRepo;
    private final EntityManager entityManager;

    public PublicationServiceImpl(@Qualifier("publicationRepo") PublicationRepo publicationRepo,
                                  @Qualifier("entityManagerFactory") EntityManager entityManager) {

        this.publicationRepo = publicationRepo;
        this.entityManager = entityManager;
    }

    @Override
    public List<Publication> findAll() {
        return publicationRepo.findAll();
    }

    @Override
    public Publication findById(int id) {
        return null;
    }

    @Override
    public List<Publication> findByTag(String tag) {
        return publicationRepo.findByTag(tag);
    }

    @Override
    public void updatePublication(String text) {

    }

    @Override
    public void deletePublication(int id) {

    }

    @Override
    public void save(Publication message) {
        publicationRepo.save(message);
    }
}
