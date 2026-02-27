package com.example.starsOfLifeBot.baza;

import com.example.starsOfLifeBot.model.second.Zadiak;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotZadiakRepa extends CrudRepository<Zadiak,Long> {
}
