package com.example.starsOfLifeBot.model.first;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Data
@Entity
@Table(name = "persons")
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    @Id
    private long tgid;
    @Column(name = "username")
    private String userName;
    @Column(name = "firstname")
    private String firstName;
    @Column(name = "lastname")
    private String lastName;
    private Date register;
    private String name;
    private LocalDate bithday;
    @Column(name = "registrationprocess")
    private RegistrationProcess registrationProcess;
    private long chatid;
    private LocalTime time;
    private String sity;
}
