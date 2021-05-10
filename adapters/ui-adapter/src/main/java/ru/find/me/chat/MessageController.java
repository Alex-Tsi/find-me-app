package ru.find.me.chat;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;
import ru.find.me.RoomServiceImpl;
import ru.find.me.UserService;
import ru.find.me.dao.chat.MessageRepo;
import ru.find.me.model.User;
import ru.find.me.model.chat.Message;
import ru.find.me.model.chat.Room;

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
                             UserService userService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.messageRepo = messageRepo;
        this.roomService = roomService;
        this.userService = userService;
    }

    @MessageMapping("/send")
    public Message sendMsg(Message message) {
        createRoomIfNotExist(message);
        simpMessagingTemplate.convertAndSendToUser(
                message.getRecipientId().toString(), "/messages", message);
        System.out.println(message);
        return message;
    }

    public void createRoomIfNotExist(Message message) {
        User sender = userService.findById(message.getSenderId());
        User recipient = userService.findById(message.getRecipientId());
        Room room = roomService.findByUsers(sender.getId(), recipient.getId());
        System.out.println(room);
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
