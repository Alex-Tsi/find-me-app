package ru.find.me.api;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.find.me.CommentService;
import ru.find.me.PublicationService;
import ru.find.me.api.dto.CommentDto;
import ru.find.me.api.dto.CommentRequest;
import ru.find.me.model.Comment;
import ru.find.me.model.Publication;
import ru.find.me.model.User;

@RestController
@RequestMapping("/api/publications/{publicationId}/comments")
public class CommentApiController {

    private final CommentService commentService;
    private final PublicationService publicationService;

    public CommentApiController(
            @Qualifier("commentServiceImpl") CommentService commentService,
            @Qualifier("publicationServiceImpl") PublicationService publicationService) {
        this.commentService = commentService;
        this.publicationService = publicationService;
    }

    @PostMapping
    public ResponseEntity<CommentDto> addComment(@AuthenticationPrincipal User user,
                                                 @PathVariable long publicationId,
                                                 @Valid @RequestBody CommentRequest request) {
        Publication publication = publicationService.findById(publicationId);
        Comment comment = new Comment();
        comment.setText(request.text());
        comment.setUser(user);
        comment.setPublication(publication);
        publication.addComments(comment);
        commentService.saveComment(comment);
        publicationService.save(publication);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiMapper.toDto(comment));
    }
}
