package ru.find.me;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.find.me.dao.CommentRepo;
import ru.find.me.model.Comment;

@Service
public class CommentServiceImpl implements ru.find.me.CommentService {

    private final CommentRepo commentRepo;
    private final EntityManager em;

    @Autowired
    public CommentServiceImpl(CommentRepo commentRepo, EntityManager em) {
        this.commentRepo = commentRepo;
        this.em = em;
    }

    @Override
    public void saveComment(Comment comment) {
        commentRepo.save(comment);
    }
}
