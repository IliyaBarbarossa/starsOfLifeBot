package com.example.starsOfLifeBot.handlers;

import com.example.starsOfLifeBot.baza.BotPersonRepa;
import com.example.starsOfLifeBot.dispetcherAndInterfase.BotMessageHandler;
import com.example.starsOfLifeBot.model.first.RegistrationProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
@Component
public class StartHandler implements BotMessageHandler {
    @Autowired
    private BotPersonRepa botPersonRepa;

    @Override
    public String getName() {
        return "start";
    }

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.getMessage().getChatId();
        long id = update.getMessage().getFrom().getId();
        String text = "Спроси у звезд";
        if (botPersonRepa.findById(id).get().getRegistrationProcess() == RegistrationProcess.NAME) {
            text = "Введите имя";
            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .build();
            return message;
        } else if (botPersonRepa.findById(id).get().getRegistrationProcess() == RegistrationProcess.BIRTH_DATE) {
            text = "Введите дату рождения в формате dd.mm.yyyy";
            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .build();
            return message;
        }
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        message.setReplyMarkup(ReplyKeyboardMarkup.builder()
                .oneTimeKeyboard(true)
                .resizeKeyboard(true)
                .keyboardRow(new KeyboardRow("Анкета"))
                .keyboardRow(new KeyboardRow("Обратиться к звездам"))
                .build());
        return message;
    }
}
