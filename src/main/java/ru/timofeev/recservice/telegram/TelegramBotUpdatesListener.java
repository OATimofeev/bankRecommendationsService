package ru.timofeev.recservice.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final TelegramBot telegramBot;
    private final TelegramService telegramService;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(u -> {
            log.info("Processing update: '{}'", u);
            try {
                if (u.message() == null || u.message().text() == null) {
                    return;
                }
                if (u.message().text().equals("/start")) {
                    telegramBot.execute(TelegramMessageProvider.getWelcomeMessage(u.message().chat().id()));
                    telegramBot.execute(TelegramMessageProvider.getHelpMessage(u.message().chat().id()));
                } else if (MessagePattern.RECOMMEND_PATTERN.matcher(u.message().text()).matches()) {
                    telegramService.getUser(u.message().chat().id(), u.message().text()).forEach(telegramBot::execute);
                } else {
                    telegramBot.execute(TelegramMessageProvider.getDefaultUnknownMessage(u.message().chat().id()));
                    telegramBot.execute(TelegramMessageProvider.getHelpMessage(u.message().chat().id()));
                }
            } catch (Exception e) {
                log.error("Error with processing update: chatId {}. Stacktrace: ", u.message().chat().id(), e);
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
