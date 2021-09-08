package ru.find.me.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import ru.find.me.RoomServiceImpl;
import ru.find.me.UserService;
import ru.find.me.model.User;
import ru.find.me.model.chat.Room;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ChatController {

    private final UserService userService;
    private final RoomServiceImpl roomService;

    @Autowired
    public ChatController(UserService userService, RoomServiceImpl roomService) {
        this.userService = userService;
        this.roomService = roomService;
    }

    @GetMapping("/chat/{user}")
    public String enterTheChat(@ModelAttribute("room") Room room,
                               @PathVariable User user,
                               Model model) {
        List<Room> rooms = user.getChatRooms();
        List<User> userList = extractUsersConformityDialogues(rooms, user);
        addModelIfChatSelected(model, room, user);
//        model.addAttribute("room", room);
        model.addAttribute("users", userList);
        return "chat/chat";
    }

    public List<User> extractUsersConformityDialogues(List<Room> rooms, User user) {
        return rooms.stream().map(Room::getUsers).flatMap(Collection::stream)
                .filter(u -> !u.equals(user)).collect(Collectors.toList());
    }

    public void addModelIfChatSelected(Model model, Room room, User user) {
        if (room.getId() > 0) {
            User companion = roomService.findCompanionByUserIdAndRoomId(user.getId(), room.getId());
            model.addAttribute("room", room);
            model.addAttribute("companion", companion);
        }
    }
}
