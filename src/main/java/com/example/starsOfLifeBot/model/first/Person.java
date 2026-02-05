package com.example.starsOfLifeBot.model.first;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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




    public long getTgid() {
        return tgid;
    }

    public void setTgid(long tgid) {
        this.tgid = tgid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBithday() {
        return bithday;
    }

    public void setBithday(LocalDate bithday) {
        this.bithday = bithday;
    }

    public RegistrationProcess getRegistrationProcess() {
        return registrationProcess;
    }

    public void setRegistrationProcess(RegistrationProcess registrationProcess) {
        this.registrationProcess = registrationProcess;
    }

    public Person(long tgId, String userName, String firstName, String lastName, Date register, String name, RegistrationProcess registrationProcess, LocalDate bithday, long chatid) {
        this.tgid = tgId;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.register = register;
        this.name = name;
        this.registrationProcess = registrationProcess;
        this.bithday = bithday;
        this.chatid=chatid;

    }
}
