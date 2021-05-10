package ru.find.me.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
public class Publication {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String title;

    private String description;

    private String tags;

    private String fileName;

    private String date;

    private String motivations;

    private String rewards;

    private String whoNeed;

    @ToString.Exclude
    @OneToMany(mappedBy = "publication", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Comment> comments;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User author;

    public String getAuthorName() {
        return author != null ? author.getUsername() : "<none>";
    }

    public Publication(String description, String tags, User author) {
        this.description = description;
        this.tags = tags;
        this.author = author;
    }

    public void addComments(Comment comment) {
        comments = Objects.requireNonNullElseGet(comments, ArrayList::new);
        comments.add(comment);
    }

    public void deleteComment(Comment comment) {
        comments = Objects.requireNonNullElseGet(comments, ArrayList::new);
        comments.remove(comment);
    }
}
