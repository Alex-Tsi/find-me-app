package ru.find.me;

import ru.find.me.model.Comment;
import ru.find.me.model.User;

public interface CommentService {

    void addCommentToUser(User user, Comment comment);

}
