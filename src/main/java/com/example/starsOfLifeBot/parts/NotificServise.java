package com.example.starsOfLifeBot.parts;

import com.example.starsOfLifeBot.baza.BotPersonRepa;
import com.example.starsOfLifeBot.model.first.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Service
public class NotificServise {
    @Autowired
    private BotPersonRepa botPersonRepa;
    @Autowired
    private TelegramClient telegramClient;

    @Scheduled(cron = "0 4 0 * * *")
    public void spam() throws TelegramApiException {

        long count = botPersonRepa.count();
        List<Person> listp = new ArrayList<>();
        botPersonRepa.findAll().forEach(a -> listp.add(a));


        for (int i = 0; i < listp.size(); i++) {
            if (listp.get(i).getFirstName().equals("Иль")){
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(listp.get(i).getChatid())
                    .text("Заходи-проверяй!")
//            "Давно не узнавали свой астрологический прогноз?\n"+"Заходи скороей и нажимай /start"
                    .build();
            telegramClient.execute(sendMessage);
        }
    }


}
}
