package ru.find.me.api;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.find.me.RoomService;
import ru.find.me.UserService;
import ru.find.me.api.dto.DialogDto;
import ru.find.me.api.dto.MessageDto;
import ru.find.me.api.dto.UserDto;
import ru.find.me.model.User;
import ru.find.me.model.chat.Room;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/chat")
public class ChatApiController {

    private final RoomService roomService;
    private final UserService userService;

    public ChatApiController(@Qualifier("roomServiceImpl") RoomService roomService,
                             @Qualifier("userServiceImpl") UserService userService) {
        this.roomService = roomService;
        this.userService = userService;
    }

    /** Все пользователи, кроме текущего — для старта нового диалога из UI. */
    @GetMapping("/users")
    public List<UserDto> users(@AuthenticationPrincipal User user) {
        return userService.findAll().stream()
                .filter(u -> !Objects.equals(u.getId(), user.getId()))
                .map(ApiMapper::toDto)
                .toList();
    }

    /** Список диалогов текущего пользователя: комната + собеседник. */
    @GetMapping("/dialogs")
    public List<DialogDto> dialogs(@AuthenticationPrincipal User user) {
        if (user.getChatRooms() == null) {
            return List.of();
        }
        return user.getChatRooms().stream()
                .map(room -> {
                    User companion = roomService.findCompanionByUserIdAndRoomId(user.getId(), room.getId());
                    return new DialogDto(room.getId(), ApiMapper.toDto(companion));
                })
                .toList();
    }

    /** История сообщений комнаты (доступна только её участникам). */
    @GetMapping("/rooms/{roomId}/messages")
    public List<MessageDto> messages(@AuthenticationPrincipal User user, @PathVariable long roomId) {
        Room room = roomService.findRoomById(roomId);
        if (!isParticipant(room, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Вы не участник этой комнаты");
        }
        if (room.getMessages() == null) {
            return List.of();
        }
        return room.getMessages().stream().map(ApiMapper::toDto).toList();
    }

    private boolean isParticipant(Room room, User user) {
        return Objects.equals(room.getFirstUserId(), user.getId())
                || Objects.equals(room.getSecondUserId(), user.getId());
    }
}
