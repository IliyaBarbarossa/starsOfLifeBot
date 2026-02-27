package com.example.starsOfLifeBot.handlers;

import com.example.starsOfLifeBot.baza.BotPersonRepa;
import com.example.starsOfLifeBot.baza.BotZPrognozRepa;
import com.example.starsOfLifeBot.baza.BotZadiakRepa;
import com.example.starsOfLifeBot.dispetcherAndInterfase.BotMessageHandler;
import com.example.starsOfLifeBot.ii.IiServiseKarma;
import com.example.starsOfLifeBot.model.second.Zadiak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;

@Component
public class KarmaHandler implements BotMessageHandler {
    @Autowired
    private BotPersonRepa botPersonRepa;
    @Autowired
    private BotZPrognozRepa botZPrognozRepa;
    @Autowired
    private BotZadiakRepa botZadiakRepa;
    @Autowired
    private IiServiseKarma iiServiseKarma;

    @Override
    public String getName() {
        return "Karma";
    }

    @Override
    public PartialBotApiMethod<Message> handle(Update update) {
        Long id = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();

        String caption = "";
        if (!botZadiakRepa.existsById(id)) {
            LocalDate bithday = botPersonRepa.findById(id).get().getBithday();
            botZadiakRepa.save(new Zadiak(id, zodiak(bithday)));
        }
        if (botZPrognozRepa.findById(botZadiakRepa.findById(id).get().getZadiak()).get().getPrognoz() == null) {
            iiServiseKarma.getAIZadiakResponse();
        } else {
            caption = " Твоя кармическая задача: " + botZPrognozRepa.findById(botZadiakRepa.findById(id).get().getZadiak()).get().getPrognoz();
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

    public static String zodiak(LocalDate parse) {
        int year = parse.getYear();
        if (parse.isBefore(LocalDate.parse("19.01." + year))) {
            return "Козерог";
        } else if (parse.isBefore(LocalDate.parse("18.02." + year))) {
            return "Водолей";
        } else if (parse.isBefore(LocalDate.parse("20.03." + year))) {
            return "Рыбы";
        } else if (parse.isBefore(LocalDate.parse("19.04." + year))) {
            return "Овен";
        } else if (parse.isBefore(LocalDate.parse("20.05." + year))) {
            return "Телец";
        } else if (parse.isBefore(LocalDate.parse("20.06." + year))) {
            return "Близнецы";
        } else if (parse.isBefore(LocalDate.parse("22.07." + year))) {
            return "Рак";
        } else if (parse.isBefore(LocalDate.parse("22.08." + year))) {
            return "Лев";
        } else if (parse.isBefore(LocalDate.parse("22.09." + year))) {
            return "Дева";
        } else if (parse.isBefore(LocalDate.parse("22.10." + year))) {
            return "Весы";
        } else if (parse.isBefore(LocalDate.parse("21.11." + year))) {
            return "Скорпион";
        } else if (parse.isBefore(LocalDate.parse("21.12." + year))) {
            return "Стрелец";
        } else {
            return "Козерог";
        }
    }
}
