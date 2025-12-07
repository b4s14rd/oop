package ru.app.consultation;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        String dataFilePath = "instruction_patching_data.txt";

        QuestionRepository repository = new QuestionRepository(dataFilePath);//инициализация логики
        DialogLogic logic = new DialogLogic(repository);

        try {//запуск телеграм бот апи
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

            botsApi.registerBot(new TelegramConsultationBot(logic));//регистрация

            System.out.println("Telegram Bot запущен и готов к работе!");

        } catch (TelegramApiException e) {
            System.err.println("Ошибка при запуске Telegram-бота: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
//инициализирует репозиторий данных, создает основную логику бота и запускает Telegram API
