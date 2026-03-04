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


import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;


import org.geonames.Toponym;
import org.geonames.WebService;
import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;

@Component
public class StarsBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private String token;
    private final TelegramClient telegramClient;
    private final BotPersonRepa botPersonRepa;
    private final BotPrognozRepa botPrognozRepa;
    private final BotZadiakRepa botZadiakRepa;
    private final IiServise iiServise;
    private final BotMessageDispatcher botMessageDispatcher;

    public StarsBot(@Value("${tg.api.token}") String token, TelegramClient telegramClient, BotPersonRepa botPersonRepa, BotPrognozRepa botPrognozRepa, IiServise iiServise, BotMessageDispatcher botMessageDispatcher, BotZadiakRepa botZadiakRepa) {
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
                if (!botPersonRepa.existsById(update.getMessage().getFrom().getId())) {
                    newPerson(update);
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
            } else if (update.getMessage().getText().equals("Изменить Анкету")) {
                updateAnketa(update);
                handlerName = "start";
            } else if (update.getMessage().getText().equals("Назад")) {
                handlerName = "start";
            } else if (isValidDate(update.getMessage().getText())) {
                handlerName = saveBithday(update);
            } else if (isValidDateTime(update.getMessage().getText())) {
                handlerName = saveBithdayTime(update);
            } else if (botPersonRepa.findById(update.getMessage().getFrom().getId()).get().getRegistrationProcess() == RegistrationProcess.SITY) {
                try {
                    saveSityy(update);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                handlerName = "start";
            } else {
                if (botPersonRepa.findById(update.getMessage().getFrom().getId()).get().getRegistrationProcess() != RegistrationProcess.NAME) {
                    handlerName = "delite";
                } else {
                    saveName(update);
                    handlerName = "start";
                }
            }
            PartialBotApiMethod<Message> apiMessage = botMessageDispatcher.dispatch(handlerName, update);

            try {
                if (apiMessage instanceof SendMessage sendMessage) {
                    telegramClient.execute(sendMessage);
                } else if (apiMessage instanceof SendPhoto sendPhoto) {
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

    public static boolean isValidDateTime(String input) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        try {
            LocalTime time = LocalTime.parse(input, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static String zodiak(LocalDate parse) {
        int year = parse.getYear();

        if (parse.isBefore(LocalDate.of(year, Month.JANUARY, 19))) {
            return "Козерог";
        } else if (parse.isBefore(LocalDate.of(year, Month.FEBRUARY, 18))) {
            return "Водолей";
        } else if (parse.isBefore(LocalDate.of(year, Month.MARCH, 20))) {
            return "Рыбы";
        } else if (parse.isBefore(LocalDate.of(year, Month.APRIL, 19))) {
            return "Овен";
        } else if (parse.isBefore(LocalDate.of(year, Month.MAY, 20))) {
            return "Телец";
        } else if (parse.isBefore(LocalDate.of(year, Month.JUNE, 20))) {
            return "Близнецы";
        } else if (parse.isBefore(LocalDate.of(year, Month.JULY, 22))) {
            return "Рак";
        } else if (parse.isBefore(LocalDate.of(year, Month.AUGUST, 22))) {
            return "Лев";
        } else if (parse.isBefore(LocalDate.of(year, Month.SEPTEMBER, 22))) {
            return "Дева";
        } else if (parse.isBefore(LocalDate.of(year, Month.OCTOBER, 22))) {
            return "Весы";
        } else if (parse.isBefore(LocalDate.of(year, Month.NOVEMBER, 21))) {
            return "Скорпион";
        } else if (parse.isBefore(LocalDate.of(year, Month.DECEMBER, 21))) {
            return "Стрелец";
        } else {
            return "Козерог";
        }
    }

    public void newPerson(Update update) {
        Person us = new Person(
                update.getMessage().getFrom().getId(),
                update.getMessage().getFrom().getUserName(),
                update.getMessage().getFrom().getFirstName(),
                update.getMessage().getFrom().getLastName(),
                new Date(),
                null,
                null,
                RegistrationProcess.NAME,
                update.getMessage().getChatId(),
                null, null);
        botPersonRepa.save(us);
    }

    public void updateAnketa(Update update) {
        Person person = botPersonRepa.findById(update.getMessage().getFrom().getId()).get();
        person.setRegistrationProcess(RegistrationProcess.NAME);
        person.setTime(null);
        person.setName(null);
        person.setBithday(null);
        person.setSity(null);
        botPersonRepa.save(person);

    }

    public String saveBithday(Update update) {
        if (botPersonRepa.findById(update.getMessage().getFrom().getId()).get().getRegistrationProcess() != RegistrationProcess.BIRTH_DATE) {
            return "delite";
        } else {
            Person p = botPersonRepa.findById(update.getMessage().getFrom().getId()).get();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDate parse = LocalDate.parse(update.getMessage().getText(), formatter);
            p.setBithday(parse);
            p.setRegistrationProcess(RegistrationProcess.BIRTH_TIME);
            Zadiak zadiak = new Zadiak(update.getMessage().getFrom().getId(), zodiak(parse));
            botPersonRepa.save(p);
            botZadiakRepa.save(zadiak);
            return "start";
        }
    }


    public void saveSityy(Update update) throws Exception {
        Person p = botPersonRepa.findById(update.getMessage().getFrom().getId()).get();
        String text = update.getMessage().getText();
        String s = serchSity(text);
        if (!s.isEmpty()) {

            p.setSity(s);
            p.setRegistrationProcess(RegistrationProcess.COMPLETE);
            botPersonRepa.save(p);
        }

    }

    public String serchSity(String args) throws Exception {

        WebService.setUserName("iliya168");
        ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
        searchCriteria.setQ(args);
        searchCriteria.setMaxRows(1);
        ToponymSearchResult result = WebService.search(searchCriteria);
        List<Toponym> toponyms = result.getToponyms();
        if (toponyms.size() > 0) {
            Toponym toponym = toponyms.get(0);
            return toponym.getName() + ", " + toponym.getCountryName();
        } else {
            return "";
        }

    }


    public String saveBithdayTime(Update update) {
        if (botPersonRepa.findById(update.getMessage().getFrom().getId()).get().getRegistrationProcess() != RegistrationProcess.BIRTH_TIME) {
            return "delite";
        } else {
            Person p = botPersonRepa.findById(update.getMessage().getFrom().getId()).get();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime parse = LocalTime.parse(update.getMessage().getText(), formatter);
            p.setTime(parse);
            p.setRegistrationProcess(RegistrationProcess.SITY);
            botPersonRepa.save(p);
            return "start";
        }
    }

    public void saveName(Update update) {
        Person p = botPersonRepa.findById(update.getMessage().getFrom().getId()).get();
        p.setName(update.getMessage().getText());
        p.setRegistrationProcess(RegistrationProcess.BIRTH_DATE);
        botPersonRepa.save(p);
    }


}
