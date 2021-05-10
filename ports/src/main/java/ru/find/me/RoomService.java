package ru.find.me;

import ru.find.me.model.User;
import ru.find.me.model.chat.Room;

public interface RoomService {

    Room findByUsers(long firstId, long secondId);

    void saveRoom(Room room);

    User findCompanionByUserIdAndRoomId(long userId, long roomId);
}
