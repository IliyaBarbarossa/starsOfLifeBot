package com.example.starsOfLifeBot.handlers;

import com.example.starsOfLifeBot.baza.BotPersonRepa;
import com.example.starsOfLifeBot.dispetcherAndInterfase.BotMessageHandler;
import com.example.starsOfLifeBot.model.first.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class AdminHandler implements BotMessageHandler {
    private BotPersonRepa botPersonRepa;

    @Override
    public String getName() {
        return "admin";
    }

    @Override
    public SendMessage handle(Update update) {
        SendMessage message = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("Привет админ")
                .build();
        message.setReplyMarkup(ReplyKeyboardMarkup.builder()
                .oneTimeKeyboard(true)
                .resizeKeyboard(true)
                .keyboardRow(new KeyboardRow("Отправить рассылку"))
                .keyboardRow(new KeyboardRow("Стереть прогноз"))
                .keyboardRow(new KeyboardRow("Стереть карму"))
                .build());
        return message;
    }
}
