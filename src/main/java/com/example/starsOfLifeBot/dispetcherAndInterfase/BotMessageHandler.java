package com.example.starsOfLifeBot.dispetcherAndInterfase;

import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.io.Serializable;

public interface BotMessageHandler {
    String getName();

    PartialBotApiMethod<Message> handle(Update update);
}
