package ru.find.me.api;

import ru.find.me.api.dto.CommentDto;
import ru.find.me.api.dto.MessageDto;
import ru.find.me.api.dto.ProfileDto;
import ru.find.me.api.dto.PublicationDto;
import ru.find.me.api.dto.UserDto;
import ru.find.me.model.Comment;
import ru.find.me.model.Profile;
import ru.find.me.model.Publication;
import ru.find.me.model.User;
import ru.find.me.model.chat.Message;

import java.util.List;

/**
 * Ручное преобразование доменных сущностей в DTO. Наружу отдаём только DTO,
 * чтобы не сериализовать двунаправленные EAGER-связи сущностей (рекурсия/раздувание).
 */
public final class ApiMapper {

    private ApiMapper() {
    }

    public static UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(user.getId(), user.getUsername());
    }

    public static CommentDto toDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), toDto(comment.getUser()));
    }

    public static PublicationDto toDto(Publication p) {
        List<CommentDto> comments = p.getComments() == null
                ? List.of()
                : p.getComments().stream().map(ApiMapper::toDto).toList();
        return new PublicationDto(
                p.getId(),
                p.getTitle(),
                p.getDescription(),
                p.getTags(),
                p.getFileName(),
                p.getDate(),
                p.getMotivations(),
                p.getRewards(),
                p.getWhoNeed(),
                toDto(p.getAuthor()),
                comments);
    }

    public static ProfileDto toDto(Profile p) {
        return new ProfileDto(
                p.getId(),
                p.getFirstName(),
                p.getLastName(),
                p.getPhoneNumber(),
                p.getEmail(),
                p.getAvatar(),
                p.getCountry(),
                p.getSex(),
                p.getRegistrationDate(),
                p.getCity(),
                p.getAddress(),
                p.getAge(),
                p.getDescription(),
                p.getSkills());
    }

    public static MessageDto toDto(Message m) {
        return new MessageDto(
                m.getId(),
                m.getContent(),
                m.getSenderId(),
                m.getRecipientId(),
                m.getSenderName(),
                m.getRecipientName());
    }
}
