package ru.find.me.chat;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.find.me.RoomService;
import ru.find.me.model.chat.Room;

@Controller
public class SelectChatRoomController {

    private final RoomService service;

    public SelectChatRoomController(RoomService service) {
        this.service = service;
    }

    @GetMapping("/select-room")
    public String getChatRoom(@RequestParam("currentUser") long currentUserId,
                              @RequestParam("companionUser") long companionId,
                              RedirectAttributes redirectAttributes) {
        Room room = service.findByUsers(currentUserId, companionId);
        System.out.println(room);
        redirectAttributes.addFlashAttribute("room", room);
        return "redirect:/chat/" + currentUserId;
    }
}
