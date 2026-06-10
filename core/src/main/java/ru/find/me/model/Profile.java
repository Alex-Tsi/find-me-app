package ru.find.me.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

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
