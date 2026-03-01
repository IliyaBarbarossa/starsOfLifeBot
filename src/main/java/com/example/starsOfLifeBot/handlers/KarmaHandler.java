package com.example.starsOfLifeBot.handlers;

import com.example.starsOfLifeBot.baza.BotPersonRepa;
import com.example.starsOfLifeBot.baza.BotZPrognozRepa;
import com.example.starsOfLifeBot.baza.BotZadiakRepa;
import com.example.starsOfLifeBot.bot.StarsBot;
import com.example.starsOfLifeBot.dispetcherAndInterfase.BotMessageHandler;
import com.example.starsOfLifeBot.ii.IiServiseKarma;
import com.example.starsOfLifeBot.model.second.ZPrognoz;
import com.example.starsOfLifeBot.model.second.Zadiak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
    @Value("${srarsbot.sendPhoto}")
    private boolean sendPhoto = false;

    @Override
    public PartialBotApiMethod<Message> handle(Update update) {
        Long id = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();

        String caption = "";
        if (!botZadiakRepa.existsById(id)) {
            LocalDate bithday = botPersonRepa.findById(id).get().getBithday();
            botZadiakRepa.save(new Zadiak(id, StarsBot.zodiak(bithday)));
        }
        ZPrognoz zPrognoz = botZPrognozRepa.findById(botZadiakRepa.findById(id).get().getZadiak()).get();
        if (zPrognoz.getPrognoz() == null || zPrognoz.getDate().isBefore(LocalDate.now())) {
            iiServiseKarma.getAIZadiakResponse();
        } else {
            caption = " Твоя кармическая задача: " + botZPrognozRepa.findById(botZadiakRepa.findById(id).get().getZadiak()).get().getPrognoz();
        }

        if(sendPhoto) {
            SendPhoto.SendPhotoBuilder<?, ?> photoBuilder = SendPhoto.builder()
                    .chatId(chatId)
                    .caption(caption + "\n" + "/start");


            InputStream is = getClass().getClassLoader()
                    .getResourceAsStream("photo/chad_ecc9af62b6064313a83cc243404e2ec2_3.png");
            InputFile photo = new InputFile().setMedia(is, "stars");
            photoBuilder.photo(photo);


            return photoBuilder.build();
        }else {
            return SendMessage.builder()
                    .chatId(chatId)
                    .text(caption + "\n" + "/start")
                    .build();
        }
    }
}
