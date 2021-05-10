package ru.find.me;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.find.me.dao.PublicationRepo;
import ru.find.me.model.Publication;

import javax.persistence.EntityManager;
import javax.persistence.Query;
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
    public Publication findById(long id) {
        return publicationRepo.findById(id).orElseThrow(() -> new RuntimeException("not exist"));
    }

    @Override
    public List<Publication> findByTags(String longTag) {
        String[] tags = longTag.split(" ");
        String selectQuery = "SELECT p FROM Publication p WHERE ";
        StringBuilder insertQuery = new StringBuilder();
        for (String s : tags) {
            insertQuery.append("p.tags LIKE ");
            insertQuery.append("'%");
            insertQuery.append(s);
            insertQuery.append("%' OR ");
        }

        insertQuery.delete(insertQuery.length() - 3, insertQuery.length());
        Query query = entityManager.createQuery(selectQuery + insertQuery);
        List<Publication> list = (List<Publication>) query.getResultList();
        return list;
    }

    @Override
    public void updatePublication(String text) {

    }

    @Override
    public void deletePublication(long id) {
        publicationRepo.deleteById(id);
    }

    @Override
    public void save(Publication publication) {
        publicationRepo.save(publication);
    }
}
