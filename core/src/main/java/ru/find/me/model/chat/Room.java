package ru.find.me.model.chat;

import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import ru.find.me.model.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ToString.Exclude
    @ManyToMany(mappedBy = "chatRooms", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<User> users;

    private Long firstUserId;

    private Long secondUserId;

    @ToString.Exclude
    @OneToMany(mappedBy = "room", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Message> messages;

    public void addMessage(Message message) {
        messages = Objects.requireNonNullElseGet(messages, ArrayList::new);
        messages.add(message);
    }

    public void deleteMessage(Message message) {
        messages = Objects.requireNonNullElseGet(messages, ArrayList::new);
        messages.remove(message);
    }

    public void addUser(User user) {
        users = Objects.requireNonNullElseGet(users, ArrayList::new);
        users.add(user);
    }

    public void deleteUser(User user) {
        users = Objects.requireNonNullElseGet(users, ArrayList::new);
        users.remove(user);
    }
}
