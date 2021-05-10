package ru.find.me.dao.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.find.me.model.chat.Room;


public interface RoomRepo extends JpaRepository<Room, Long> {

    @Query("SELECT r FROM Room r WHERE (r.firstUserId = ?1 AND r.secondUserId = ?2) OR " +
            "(r.secondUserId = ?1 AND r.firstUserId = ?2)")
    Room findRoomByUsersId(long first, long second);
}
