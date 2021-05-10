package ru.find.me.dao.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.find.me.model.chat.Message;

public interface MessageRepo extends JpaRepository<Message, Long> {
}
