package com.example.starsOfLifeBot.ii;

import com.example.starsOfLifeBot.baza.BotPersonRepa;
import com.example.starsOfLifeBot.baza.BotPrognozRepa;
import com.example.starsOfLifeBot.model.first.Person;
import com.example.starsOfLifeBot.model.second.Prognoz;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@Service
public class IiServise {
    @Autowired
    private BotPersonRepa botPersonRepa;
    @Autowired
    private BotPrognozRepa botPrognozRepa;

    private static final String API_URL = "https://ask.chadgpt.ru/api/public/gpt-5";
    private static final String API_KEY = "chad-7c7e799b20ae450482f747f7c03d440d16x22us4";
    public String getAIResponse(Update update) {

        try {
            Person person = botPersonRepa.findById(update.getMessage().getFrom().getId()).get();
            // Формируем JSON-запрос
            JSONObject requestJson = new JSONObject();
            requestJson.put("message", "создай астрологический прогноз на день длинной до 50 слов для человека по имени " + person.getName()+" с датой рождения "+ person.getBithday());
            requestJson.put("api_key", API_KEY);

            // Отправляем запрос
            HttpURLConnection conn = (HttpURLConnection) new URL(API_URL).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestJson.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Получаем ответ
            int statusCode = conn.getResponseCode();
            if (statusCode != 200) {
                System.out.printf("Ошибка! Код http-ответа: %d\n", statusCode);
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            JSONObject respJson = new JSONObject(response.toString());
            String response1 = respJson.getString("response");
            LocalDate part = LocalDate.now();
            Prognoz prognoz = new Prognoz(person.getTgid(),response1,part);
            botPrognozRepa.save(prognoz);
            return response1;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "не зашел в цикл";
    }

}
