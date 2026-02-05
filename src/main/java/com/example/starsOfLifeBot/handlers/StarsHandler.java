package com.example.starsOfLifeBot.handlers;

import com.example.starsOfLifeBot.baza.BotPrognozRepa;
import com.example.starsOfLifeBot.dispetcherAndInterfase.BotMessageHandler;
import com.example.starsOfLifeBot.ii.IiServise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import java.net.URL;
import java.time.LocalDate;

@Component
public class StarsHandler implements BotMessageHandler {
    @Autowired
    IiServise iiServise;
    @Autowired
    BotPrognozRepa botPrognozRepa;

    @Override
    public String getName() {
        return "stars";
    }

    @Override
    public SendPhoto handle(Update update) {
        Long id = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();

        String caption = "";
        if (botPrognozRepa.existsById(id) && botPrognozRepa.findById(id).get() != null) {
            caption = "За новым прогнозом приходи завтра! Твой прогноз на сегодня: \n\n" + botPrognozRepa.findById(id).get().getPrognoz();
        }
        else {
            caption = iiServise.getAIResponse(update);
        }


        SendPhoto.SendPhotoBuilder<?, ?> photoBuilder = SendPhoto.builder()
                .chatId(chatId)
                .caption(caption + "\n" + "/start");

        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("photo/chad_ecc9af62b6064313a83cc243404e2ec2_3.png")) {
            InputFile photo = new InputFile().setMedia(is, "stars");
            photoBuilder.photo(photo);

        } catch (IOException e) {
            //ignore
        }

        return photoBuilder.build();
    }

}
