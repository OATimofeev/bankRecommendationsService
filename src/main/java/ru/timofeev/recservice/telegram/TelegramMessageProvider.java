package ru.timofeev.recservice.telegram;

import com.pengrad.telegrambot.request.SendMessage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.timofeev.recservice.dto.recommendations.RecommendationDto;
import ru.timofeev.recservice.model.UserModel;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TelegramMessageProvider {

    public static SendMessage getWelcomeMessage(Long chatId) {
        return new SendMessage(chatId, "Добрый день! \n" +
                "Это телеграм-бот нового сервиса рекомендаций банковских продуктов \n\n" +
                "-----------------\n\n" +
                "Hello! \n" +
                "This is telegram-bot of new service of Bank recommendations!");
    }

    public static SendMessage getHelpMessage(Long chatId) {
        return new SendMessage(chatId, "Доступные команды: \n" +
                "/recommend username\n" +
                "Команда возвращает доступные рекомендации для пользователя с ником *username*\n\n" +
                "-----------------\n\n" +
                "Available commands \n" +
                "/recommend username\n" +
                "Command returns available recommendations for user with nickname *username*");
    }

    public static SendMessage getDefaultUnknownMessage(Long chatId) {
        return new SendMessage(chatId, "Неизвестная команда!\n\n" +
                "-----------------\n\n" +
                "Unknown command!");
    }

    public static SendMessage getUserNotFoundMessage(Long chatId) {
        return new SendMessage(chatId, "Пользователь не найден!\n\n" +
                "-----------------\n\n" +
                "User not found!");
    }

    public static SendMessage getHelloUserMessage(Long chatId, UserModel user) {
        return new SendMessage(chatId, "Добрый день, " + user.getFirstName() + " " + user.getLastName() + "!\n\n" +
                "-----------------\n\n" +
                "Hello, " + user.getFirstName() + " " + user.getLastName() + "!\n\n");
    }

    public static SendMessage getRecommendationMessage(Long chatId, RecommendationDto recommendation) {
        return new SendMessage(chatId, "Продукт / Product:\n" +
                recommendation.getName() + "\n\n" +
                "-----------------\n\n" +
                recommendation.getText());
    }

    public static SendMessage getNotRecommendedProductsMessage(Long chatId) {
        return new SendMessage(chatId, "К сожалению, для вас нет рекомендованых продуктов!\n\n" +
                "-----------------\n\n" +
                "Unfortunately, you have no recommended products");
    }
}
