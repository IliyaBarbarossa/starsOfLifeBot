package com.example.starsOfLifeBot.baza;

import com.example.starsOfLifeBot.model.second.Prognoz;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotPrognozRepa extends CrudRepository<Prognoz,Long> {
}
