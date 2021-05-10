package ru.find.me;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.find.me.dao.chat.RoomRepo;
import ru.find.me.model.User;
import ru.find.me.model.chat.Room;

import javax.persistence.EntityManager;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepo roomRepo;
    private final EntityManager em;
    private final UserService userService;

    @Autowired
    public RoomServiceImpl(RoomRepo roomRepo, EntityManager em, UserService userService) {
        this.roomRepo = roomRepo;
        this.em = em;
        this.userService = userService;
    }

    @Override
    public Room findByUsers(long firstId, long secondId) {

        return roomRepo.findRoomByUsersId(firstId, secondId);
    }

    @Override
    public void saveRoom(Room room) {
        roomRepo.save(room);
    }

    @Override
    public User findCompanionByUserIdAndRoomId(long userId, long roomId) {
        Room room = roomRepo.findById(roomId).orElseThrow(() -> new RuntimeException("not found"));
        long foundedId;
        if (room.getFirstUserId() == userId) {
            foundedId = room.getSecondUserId();
        } else foundedId = room.getFirstUserId();
        return userService.findById(foundedId);
    }
}
