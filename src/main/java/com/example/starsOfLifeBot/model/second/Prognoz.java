package com.example.starsOfLifeBot.model.second;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "prognoz")


public class Prognoz {
    @Id
    @Column(name = "tgid")
    private long thId;
    private String prognoz;
    private LocalDate part;



    public long getThId() {
        return thId;
    }

    public void setThId(long thId) {
        this.thId = thId;
    }

    public String getPrognoz() {
        return prognoz;
    }

    public void setPrognoz(String prognoz) {
        this.prognoz = prognoz;
    }
}
