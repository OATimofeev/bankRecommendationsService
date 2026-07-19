package ru.timofeev.recservice.telegram;

import com.pengrad.telegrambot.request.SendMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.timofeev.recservice.model.UserModel;
import ru.timofeev.recservice.service.RecommendationService;
import ru.timofeev.recservice.service.TransactionDataService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Сервис обработки Telegram‑сообщений для выдачи рекомендаций.
 * <p>
 * Разбирает входящие сообщения, извлекает из них username,
 * находит пользователя и формирует набор ответных сообщений.
 */

@Service
@Slf4j
@AllArgsConstructor
public class TelegramService {

    private RecommendationService recommendationService;
    private TransactionDataService transactionDataService;

    /**
     * Обрабатывает входящее сообщение и подготавливает ответы для Telegram.
     * <p>
     * 1. Пытается распарсить username из текста с помощью {@link MessagePattern#RECOMMEND_PATTERN}.<br>
     * 2. Если формат сообщения некорректен — возвращает сообщение об ошибке.<br>
     * 3. Если пользователь с таким username не найден — возвращает сообщение о ненайденном пользователе.<br>
     * 4. Если пользователь найден — формирует приветствие и сообщения с рекомендациями.
     *
     * @param chatId идентификатор чата Telegram
     * @param text   текст входящего сообщения
     * @return список сообщений, которые нужно отправить пользователю
     */
    public List<SendMessage> getUser(Long chatId, String text) {
        log.info("Parse username from chatId = '{}' with message = '{}'", chatId, text);

        Matcher matcher = MessagePattern.RECOMMEND_PATTERN.matcher(text);
        if (!matcher.matches()) {
            log.error("Fail parse message '{}' from chatId = '{}'! We're not suppose to be here!!!", text, chatId);
            return List.of(TelegramMessageProvider.getDefaultUnknownMessage(chatId));
        }
        String username = matcher.group(1);

        Optional<UserModel> userModelOpt = transactionDataService.getUserByUsername(username);
        if (userModelOpt.isEmpty()) {
            log.info("Not found correct user with username = '{}', return error message", username);
            return List.of(TelegramMessageProvider.getUserNotFoundMessage(chatId));
        }

        log.info("Prepare recommendation messages for chatId = '{}' ", chatId);

        List<SendMessage> messageList = Stream.concat(
                Stream.of(TelegramMessageProvider.getHelloUserMessage(chatId, userModelOpt.get())),
                recommendationService.getRecommendations(userModelOpt.get().getId())
                        .stream()
                        .map(x -> TelegramMessageProvider.getRecommendationMessage(chatId, x))
        ).collect(Collectors.toCollection(ArrayList::new));

        if (messageList.size() == 1) {
            messageList.add(TelegramMessageProvider.getNotRecommendedProductsMessage(chatId));
        }

        return messageList;
    }
}