package com.example.starsOfLifeBot.model.second;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "zprognoz")
public class ZPrognoz {
    @Id
    @Column(name = "znak")
    private String znak;
    private String prognoz;

}
