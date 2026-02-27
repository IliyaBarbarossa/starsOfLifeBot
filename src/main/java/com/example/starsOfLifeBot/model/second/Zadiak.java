package com.example.starsOfLifeBot.model.second;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "zadiak")
public class Zadiak {
    @Id
    @Column(name = "tgid")
    private long tgId;
    private String zadiak;
}
