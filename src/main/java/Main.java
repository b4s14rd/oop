import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.bots.DefaultBotOptions;

public class Main {
    public static void main(String[] args) {

        DefaultBotOptions options = ProxyConfig.getOptions();

        String dataFilePath = "instruction_patching_data.txt";
        QuestionRepository repository = new QuestionRepository(dataFilePath);

        DialogLogic logic = new DialogLogic(repository);

        TelegramConsultationBot bot = new TelegramConsultationBot(logic, options);

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

            botsApi.registerBot(bot);

            System.out.println("Бот успешно запущен!");
        } catch (TelegramApiException e) {
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