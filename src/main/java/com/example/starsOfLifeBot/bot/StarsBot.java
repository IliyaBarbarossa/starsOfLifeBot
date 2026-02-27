package com.example.starsOfLifeBot.bot;

import com.example.starsOfLifeBot.baza.BotPersonRepa;
import com.example.starsOfLifeBot.baza.BotPrognozRepa;
import com.example.starsOfLifeBot.baza.BotZadiakRepa;
import com.example.starsOfLifeBot.dispetcherAndInterfase.BotMessageDispatcher;
import com.example.starsOfLifeBot.ii.IiServise;
import com.example.starsOfLifeBot.model.first.Person;
import com.example.starsOfLifeBot.model.first.RegistrationProcess;
import com.example.starsOfLifeBot.model.second.Zadiak;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class StarsBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private String token;
    private final TelegramClient telegramClient;
    private final BotPersonRepa botPersonRepa;
    private final BotPrognozRepa botPrognozRepa;
    private final BotZadiakRepa botZadiakRepa;
    private final IiServise iiServise;
    private final BotMessageDispatcher botMessageDispatcher;

    public StarsBot(@Value("${tg.api.token}") String token, TelegramClient telegramClient, BotPersonRepa botPersonRepa, BotPrognozRepa botPrognozRepa, IiServise iiServise, BotMessageDispatcher botMessageDispatcher,BotZadiakRepa botZadiakRepa) {
        this.token = token;
        this.botMessageDispatcher = botMessageDispatcher;
        this.telegramClient = telegramClient;
        this.botPersonRepa = botPersonRepa;
        this.botPrognozRepa = botPrognozRepa;
        this.iiServise = iiServise;
        this.botZadiakRepa = botZadiakRepa;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String handlerName = null;

            if (update.getMessage().getText().equals("/start")) {
                // перенести в хэгндлер
                if (!botPersonRepa.existsById(update.getMessage().getFrom().getId())) {
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String date = now.format(formatter);

                    Person us = new Person(update.getMessage().getFrom().getId(), update.getMessage().getFrom().getUserName(), update.getMessage().getFrom().getFirstName(), update.getMessage().getFrom().getLastName(), null, date, RegistrationProcess.NAME, null,update.getMessage().getChatId() );
                    botPersonRepa.save(us);
                }
                handlerName = "start";
            } else if (update.getMessage().getText().equals("Обратиться к звездам")) {
                if (botPersonRepa.findById(update.getMessage().getFrom().getId()).get().getRegistrationProcess() != RegistrationProcess.COMPLETE) {
                    handlerName = "start";
                } else
                    handlerName = "stars";
            } else if (update.getMessage().getText().equals("Кармическая задача")) {
                    handlerName = "Karma";
            } else if (update.getMessage().getText().equals("Анкета")) {
                handlerName = "profil";
            } else if (update.getMessage().getText().equals("admin47")) {
                handlerName = "admin";
            }else if (update.getMessage().getText().equals("Изменить Анкету")) {
                Person person = botPersonRepa.findById(update.getMessage().getFrom().getId()).get();
                person.setRegistrationProcess(RegistrationProcess.NAME);
                botPersonRepa.save(person);
                handlerName = "start";
            }else if (update.getMessage().getText().equals("Назад")) {
                handlerName = "start";
            }else if (isValidDate(update.getMessage().getText())) {
                if (botPersonRepa.findById(update.getMessage().getFrom().getId()).get().getRegistrationProcess() != RegistrationProcess.BIRTH_DATE) {
                    handlerName = "delite";
                } else {
                    Person p = botPersonRepa.findById(update.getMessage().getFrom().getId()).get();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                    LocalDate parse = LocalDate.parse(update.getMessage().getText(), formatter);
                    p.setBithday(parse);
                    p.setRegistrationProcess(RegistrationProcess.COMPLETE);
                    Zadiak zadiak = new Zadiak(update.getMessage().getFrom().getId(),zodiak(parse));
                    botPersonRepa.save(p);
                    botZadiakRepa.save(zadiak);
                    handlerName = "start";
                }
            } else {
                if (botPersonRepa.findById(update.getMessage().getFrom().getId()).get().getRegistrationProcess() != RegistrationProcess.NAME) {
                    handlerName = "delite";
                } else {
                    Person p = botPersonRepa.findById(update.getMessage().getFrom().getId()).get();
                    p.setName(update.getMessage().getText());
                    p.setRegistrationProcess(RegistrationProcess.BIRTH_DATE);
                    botPersonRepa.save(p);
                    handlerName = "start";
                }
            }
            PartialBotApiMethod<Message> apiMessage = botMessageDispatcher.dispatch(handlerName, update);

            try {
                if (apiMessage instanceof SendMessage sendMessage) {
                    telegramClient.execute(sendMessage);
                }
                else if (apiMessage instanceof SendPhoto sendPhoto) {
                    telegramClient.execute(sendPhoto);
                }

            } catch (TelegramApiException e) {
                e.printStackTrace();
            }



        }

    }

    public String check(Long id) {
        if (botPersonRepa.findById(id).get().getRegistrationProcess() == RegistrationProcess.NAME) {
            return "name";
        } else if (botPersonRepa.findById(id).get().getRegistrationProcess() == RegistrationProcess.BIRTH_DATE) {
            return "bithday";
        } else {
            return null;
        }
    }


    public static boolean isValidDate(String input) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        try {
            LocalDate date = LocalDate.parse(input, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static String zodiak(LocalDate parse){
        int year = parse.getYear();
        if(parse.isBefore(LocalDate.parse("19.01."+year))){
            return "Козерог";
        } else if (parse.isBefore(LocalDate.parse("18.02."+year))){
            return "Водолей";
        }
        else if (parse.isBefore(LocalDate.parse("20.03."+year))){
            return "Рыбы";
        }
        else if (parse.isBefore(LocalDate.parse("19.04."+year))){
            return "Овен";
        }
        else if (parse.isBefore(LocalDate.parse("20.05."+year))){
            return "Телец";
        }
        else if (parse.isBefore(LocalDate.parse("20.06."+year))){
            return "Близнецы";
        }
        else if (parse.isBefore(LocalDate.parse("22.07."+year))){
            return "Рак";
        }
        else if (parse.isBefore(LocalDate.parse("22.08."+year))){
            return "Лев";
        }
        else if (parse.isBefore(LocalDate.parse("22.09."+year))){
            return "Дева";
        }else if (parse.isBefore(LocalDate.parse("22.10."+year))){
            return "Весы";
        }
        else if (parse.isBefore(LocalDate.parse("21.11."+year))){
            return "Скорпион";
        }
        else if (parse.isBefore(LocalDate.parse("21.12."+year))){
            return "Стрелец";
        }
        else {
            return "Козерог";
        }
    }

}
