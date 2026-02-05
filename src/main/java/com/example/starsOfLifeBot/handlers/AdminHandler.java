package com.example.starsOfLifeBot.handlers;

import com.example.starsOfLifeBot.baza.BotPersonRepa;
import com.example.starsOfLifeBot.dispetcherAndInterfase.BotMessageHandler;
import com.example.starsOfLifeBot.model.first.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;


public class AdminHandler implements BotMessageHandler {
    private BotPersonRepa botPersonRepa;
    @Override
    public String getName() {
        return "admin";
    }

    @Override
    public SendMessage handle(Update update) {
    return  null;
    }
}
