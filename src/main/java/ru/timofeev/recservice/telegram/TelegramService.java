package ru.timofeev.recservice.telegram;

import com.pengrad.telegrambot.request.SendMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.timofeev.recservice.model.UserModel;
import ru.timofeev.recservice.service.RecommendationProductsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@AllArgsConstructor
public class TelegramService {

    private RecommendationProductsService recommendationProductsService;

    public List<SendMessage> getUser(Long chatId, String text) {
        log.info("Parse username from chatId = '{}' with message = '{}'", chatId, text);

        Matcher matcher = MessagePattern.RECOMMEND_PATTERN.matcher(text);
        if (!matcher.matches()) {
            log.error("Fail parse message '{}' from chatId = '{}'! We're not suppose to be here!!!", text, chatId);
            return List.of(TelegramMessageProvider.getDefaultUnknownMessage(chatId));
        }
        String username = matcher.group(1);

        Optional<UserModel> userModelOpt = recommendationProductsService.getUserByUsername(username);
        if (userModelOpt.isEmpty()) {
            log.info("Not found correct user with username = '{}', return error message", username);
            return List.of(TelegramMessageProvider.getUserNotFoundMessage(chatId));
        }

        log.info("Prepare recommendation messages for chatId = '{}' ", chatId);

        List<SendMessage> messageList = Stream.concat(
                Stream.of(TelegramMessageProvider.getHelloUserMessage(chatId, userModelOpt.get())),
                recommendationProductsService.getRecommendations(userModelOpt.get().getId())
                        .stream()
                        .map(x -> TelegramMessageProvider.getRecommendationMessage(chatId, x))
        ).collect(Collectors.toCollection(ArrayList::new));

        if (messageList.size() == 1) {
            messageList.add(TelegramMessageProvider.getNotRecommendedProductsMessage(chatId));
        }

        return messageList;
    }
}