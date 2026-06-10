package ru.find.me.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;
import ru.find.me.RoomServiceImpl;
import ru.find.me.UserService;
import ru.find.me.dao.chat.MessageRepo;
import ru.find.me.model.User;
import ru.find.me.model.chat.Message;
import ru.find.me.model.chat.Room;

import java.security.Principal;

@RestController
public class MessageController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageRepo messageRepo;
    private final RoomServiceImpl roomService;
    private final UserService userService;

    @Autowired
    public MessageController(SimpMessagingTemplate simpMessagingTemplate,
                             MessageRepo messageRepo,
                             RoomServiceImpl roomService,
                             @Qualifier("userServiceImpl") UserService userService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.messageRepo = messageRepo;
        this.roomService = roomService;
        this.userService = userService;
    }

    @MessageMapping("/send")
    public void sendMsg(Message message, Principal principal) {
        // Отправитель берётся из аутентифицированной сессии, а не из тела —
        // иначе клиент мог бы слать сообщения от чужого имени.
        User sender = userService.findByUsername(principal.getName());
        User recipient = userService.findById(message.getRecipientId());

        message.setSenderId(sender.getId());
        message.setSenderName(sender.getUsername());
        message.setRecipientName(recipient.getUsername());

        createRoomIfNotExist(message, sender, recipient);

        // Доставка по имени получателя: клиент подписан на /user/queue/messages,
        // Spring резолвит user-destination в /queue/messages-user{session} нужного Principal.
        simpMessagingTemplate.convertAndSendToUser(
                recipient.getUsername(), "/queue/messages", message);
    }

    public void createRoomIfNotExist(Message message, User sender, User recipient) {
        Room room = roomService.findByUsers(sender.getId(), recipient.getId());
        if (room == null) {
            room = new Room();
            room.addUser(sender);
            room.addUser(recipient);
            room.setFirstUserId(sender.getId());
            room.setSecondUserId(recipient.getId());
            sender.addRoom(room);
            recipient.addRoom(room);
            roomService.saveRoom(room);
            userService.save(sender);
            userService.save(recipient);
        }
        room.addMessage(message);
        message.setRoom(room);
        messageRepo.save(message);
    }
}
