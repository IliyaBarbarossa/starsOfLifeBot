package com.example.starsOfLifeBot.handlers;

import com.example.starsOfLifeBot.baza.BotPersonRepa;
import com.example.starsOfLifeBot.baza.BotPrognozRepa;
import com.example.starsOfLifeBot.dispetcherAndInterfase.BotMessageHandler;
import com.example.starsOfLifeBot.model.first.Person;
import com.example.starsOfLifeBot.model.first.RegistrationProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
@Component
public class ProfilHandler implements BotMessageHandler {
    @Autowired
    private BotPersonRepa botPersonRepa;
    @Override
    public String getName() {
        return "profil";
    }

    @Override
    public SendMessage handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        Long id = update.getMessage().getFrom().getId();
        Person person = botPersonRepa.findById(id).get();
        String text = "Имя: "+person.getName()+"\n"+"Дата рождения: "+person.getBithday();
        SendMessage message = SendMessage.builder()
                .chatId(id)
                .text(text)
                .build();
        message.setReplyMarkup(ReplyKeyboardMarkup.builder()
                .oneTimeKeyboard(true)
                .resizeKeyboard(true)
                .keyboardRow(new KeyboardRow("Изменить Анкету"))
                .keyboardRow(new KeyboardRow("Назад"))
                .build());
        return message;
    }
}
