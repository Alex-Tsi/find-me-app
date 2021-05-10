package ru.find.me.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.find.me.model.Comment;

public interface CommentRepo extends JpaRepository<Comment, Long> {
}
