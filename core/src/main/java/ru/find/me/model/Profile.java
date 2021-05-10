package ru.find.me.model;

import com.sun.istack.Nullable;
import lombok.Data;
import lombok.ToString;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@ToString
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String email;

    private String avatar;

    private String country;

    private String sex;

    private String registrationDate;

    private String city;

    private String address;

    private Integer age;

    private String description;

    private String skills;

    @ToString.Exclude
    @OneToOne(mappedBy = "profile")
    private User user;
}
