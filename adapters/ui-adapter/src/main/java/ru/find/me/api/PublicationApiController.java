package ru.find.me.api;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.find.me.PublicationService;
import ru.find.me.UserService;
import ru.find.me.api.dto.PublicationDto;
import ru.find.me.api.dto.PublicationRequest;
import ru.find.me.model.Publication;
import ru.find.me.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/publications")
public class PublicationApiController {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final PublicationService publicationService;
    private final UserService userService;

    public PublicationApiController(
            @Qualifier("publicationServiceImpl") PublicationService publicationService,
            @Qualifier("userServiceImpl") UserService userService) {
        this.publicationService = publicationService;
        this.userService = userService;
    }

    @GetMapping
    public List<PublicationDto> findAll() {
        return publicationService.findAll().stream().map(ApiMapper::toDto).toList();
    }

    @GetMapping("/filter")
    public List<PublicationDto> filterByTags(@RequestParam("tags") String tags) {
        return publicationService.findByTags(tags).stream().map(ApiMapper::toDto).toList();
    }

    @GetMapping("/user/{userId}")
    public List<PublicationDto> findByUser(@PathVariable long userId) {
        User user = userService.findById(userId);
        if (user.getPublications() == null) {
            return List.of();
        }
        return user.getPublications().stream().map(ApiMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public PublicationDto findById(@PathVariable long id) {
        return ApiMapper.toDto(publicationService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PublicationDto> create(@AuthenticationPrincipal User user,
                                                 @Valid @RequestBody PublicationRequest request) {
        Publication publication = new Publication();
        publication.setAuthor(user);
        apply(request, publication);
        publicationService.save(publication);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiMapper.toDto(publication));
    }

    @PutMapping("/{id}")
    public PublicationDto update(@AuthenticationPrincipal User user,
                                 @PathVariable long id,
                                 @Valid @RequestBody PublicationRequest request) {
        Publication publication = publicationService.findById(id);
        requireOwner(publication, user);
        apply(request, publication);
        publicationService.save(publication);
        return ApiMapper.toDto(publication);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User user, @PathVariable long id) {
        Publication publication = publicationService.findById(id);
        requireOwner(publication, user);
        publicationService.deletePublication(id);
        return ResponseEntity.noContent().build();
    }

    private void apply(PublicationRequest request, Publication publication) {
        publication.setTitle(request.title());
        publication.setDescription(request.description());
        publication.setTags(request.tags());
        publication.setMotivations(request.motivations());
        publication.setRewards(request.rewards());
        publication.setWhoNeed(request.whoNeed());
        if (request.fileName() != null) {
            publication.setFileName(request.fileName());
        }
        publication.setDate(LocalDateTime.now().format(DATE_FORMAT));
    }

    private void requireOwner(Publication publication, User user) {
        if (publication.getAuthor() == null || publication.getAuthor().getId() != user.getId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Это не ваша публикация");
        }
    }
}
