package com.example.starsOfLifeBot.dispetcherAndInterfase;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Component
public class BotMessageDispatcher {
    private Map<String, BotMessageHandler> handlers = new HashMap<>();

    public BotMessageDispatcher(List<BotMessageHandler> handlers) {
        for (BotMessageHandler handler: handlers) {
            this.handlers.put(handler.getName(), handler);
        }
    }

    public PartialBotApiMethod<Message> dispatch(String name, Update update) {
        BotMessageHandler botMessageHandler = handlers.get(name);
        return botMessageHandler.handle(update);
    }
}
