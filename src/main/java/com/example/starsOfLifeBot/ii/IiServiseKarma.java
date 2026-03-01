package com.example.starsOfLifeBot.ii;

import com.example.starsOfLifeBot.baza.BotPersonRepa;
import com.example.starsOfLifeBot.baza.BotZPrognozRepa;
import com.example.starsOfLifeBot.baza.BotZadiakRepa;
import com.example.starsOfLifeBot.model.first.Person;
import com.example.starsOfLifeBot.model.second.Prognoz;
import com.example.starsOfLifeBot.model.second.ZPrognoz;
import com.example.starsOfLifeBot.model.second.Zadiak;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class IiServiseKarma {

    @Autowired
    private BotZPrognozRepa zPrognozRepa;
    @Autowired
    private BotPersonRepa botPersonRepa;
    @Autowired
    private BotZadiakRepa botZadiakRepa;


    private static final String API_URL = "https://ask.chadgpt.ru/api/public/gpt-5";
    @Value("${gpt.api.token}")
    private String API_KEY;

    public void getAIZadiakResponse() {

        try {
            List<ZPrognoz> list = new ArrayList<>();
            zPrognozRepa.findAll().forEach(list::add);


            for (int i = 0; i < list.size(); i++) {
                // Формируем JSON-запрос
                JSONObject requestJson = new JSONObject();
                requestJson.put("message", "создай кармическую задачу на день длинной до 15 слов для " + list.get(i).getZnak());
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
                ZPrognoz prognoz = new ZPrognoz(list.get(i).getZnak(), response1, LocalDate.now());
                zPrognozRepa.save(prognoz);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
