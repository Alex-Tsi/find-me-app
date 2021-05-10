package ru.find.me.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.find.me.UserService;
import ru.find.me.dao.chat.RoomRepo;

@Controller
@RequestMapping("/")
public class GreetingController {

    private final UserService service;

    private final RoomRepo roomRepo;

    public GreetingController(UserService service, RoomRepo roomRepo) {
        this.service = service;
        this.roomRepo = roomRepo;
    }

    @RequestMapping("/")
    public String authorization() {
        return "greeting";
    }


    @GetMapping("/a")
    public String test() {
        return "chat/chat";
    }

}
