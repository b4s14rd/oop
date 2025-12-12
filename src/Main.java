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
//непонятно как комментировать xml файл поэтому описание будет здесь: <properties> - свойства
//<maven.compiler.source> - указывает что код на Java 17
//<project.build.sourceEncoding> - устанавливает кодировку UTF - 8
//<dependencies> - зависимости
//telegrambots - обеспечивает базовый функционал для связи с Telegram API (получение и отправка сообщений)
//telegrambots-abilities - дополнительный модуль к библиотеке telegrambots, который упрощает обработку команд и ведение сложных диалогов с ботом (пока не использовали)