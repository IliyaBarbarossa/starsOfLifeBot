package com.example.starsOfLifeBot.handlers;

import com.example.starsOfLifeBot.dispetcherAndInterfase.BotMessageHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
@Component
public class DeliteHandler implements BotMessageHandler {
    @Override
    public String getName() {
        return "delite";
    }

    @Override
    public SendMessage handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Звезды вас не слышат!\n \n"+"Попробуйте наладить связь с космосом /start")
                .build();
        return message;
    }
}
