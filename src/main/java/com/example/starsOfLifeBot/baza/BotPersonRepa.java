package com.example.starsOfLifeBot.baza;

import com.example.starsOfLifeBot.model.first.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotPersonRepa extends CrudRepository<Person,Long> {
}
