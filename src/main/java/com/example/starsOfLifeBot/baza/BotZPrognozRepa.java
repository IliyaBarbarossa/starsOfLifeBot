package com.example.starsOfLifeBot.baza;

import com.example.starsOfLifeBot.model.second.ZPrognoz;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotZPrognozRepa extends CrudRepository<ZPrognoz,String> {
    
}
