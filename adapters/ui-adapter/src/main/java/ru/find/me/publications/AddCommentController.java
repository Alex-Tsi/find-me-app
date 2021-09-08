package ru.find.me.publications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.find.me.PublicationService;
import ru.find.me.dao.CommentRepo;
import ru.find.me.model.Comment;
import ru.find.me.model.Publication;
import ru.find.me.model.User;

@Controller
public class AddCommentController {

    private final CommentRepo commentService;
    private final PublicationService publicationService;

    @Autowired
    public AddCommentController(CommentRepo commentService, PublicationService publicationService) {
        this.commentService = commentService;
        this.publicationService = publicationService;
    }

    @PostMapping("/add-comment")
    public String addComment(@AuthenticationPrincipal User user,
                             @RequestParam("text") String text,
                             @RequestParam("publication_id") long id
    ) {
        Publication publication = publicationService.findById(id);
        Comment comment = new Comment();
        comment.setText(text);
        comment.setUser(user);
        comment.setPublication(publication);
        user.addComments(comment);
        publication.addComments(comment);
        commentService.save(comment);
        publicationService.save(publication);
        return "redirect:/publications/browse/" + id;
    }
}
