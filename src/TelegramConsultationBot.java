import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class TelegramConsultationBot extends TelegramLongPollingBot {//основной класс тг бота, обрабатывающий входящие сообщения и использующий DialogLogic для получения ответов

    private final DialogLogic dialogLogic;
    private static final String BOT_TOKEN = "8510472348:AAEduNxBNasRNUWvkgS9Lf1Eo0OIdFBbRe4";
    private static final String BOT_USERNAME = "@PatchingConsultationBot";

    public TelegramConsultationBot(DialogLogic dialogLogic) {
        this.dialogLogic = dialogLogic;
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {//метод вызывается каждый раз когда приходит новое сообщение или кнопка
        if (update.hasMessage() && update.getMessage().hasText()) {//проверяем, что это сообщение, и что оно содержит текст
            String userInput = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (userInput.equalsIgnoreCase("/start")) {//отправляем приветственное сообщение и список вопросов при старте
                sendResponse(chatId, new BotResponse(dialogLogic.getWelcomeMessage()));
                sendResponse(chatId, dialogLogic.getQuestionsListResponse());
            } else {//обрабатываем ввод через DialogLogic
                BotResponse botResponse = dialogLogic.processInput(userInput);//бот передает текст (userInput) в dialogLogic.processInput() и получает готовый BotResponse
                sendResponse(chatId, botResponse);//полученный ответ передается в метод sendResponse()
            }
        }
    }

    private void sendResponse(long chatId, BotResponse response) {//метод отвечает за преобразование внутреннего объекта BotResponse в формат, понятный тг
        SendMessage message = new SendMessage();
        message.setChatId(chatId);//создается объект SendMessage с нужным chatId (кому отправлять) и текстом
        message.setText(response.getText());
        message.setParseMode("HTML"); //включаем HTML разметку для жирного шрифта

        if (!response.getKeyboardButtons().isEmpty()) {//если в BotResponse есть кнопки (!response.getKeyboardButtons()(список списков, который содержит все данные для кнопок).isEmpty()), вызывается метод createKeyboard() для их создания
            message.setReplyMarkup(createKeyboard(response.getKeyboardButtons()));
        }

        try {
            execute(message);//финальный вызов Telegram API, который отправляет сообщение пользователю
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private ReplyKeyboardMarkup createKeyboard(List<List<String>> buttonData) {//метод преобразует простую структуру List<List<String>> (которую использует DialogLogic) в сложный объект ReplyKeyboardMarkup, который требует Telegram API
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);//клавиатура будет уменьшаться под размер экрана
        keyboardMarkup.setOneTimeKeyboard(false);//клавиатура остается после отправки сообщения

        List<KeyboardRow> keyboard = new ArrayList<>();

        for (List<String> rowData : buttonData) {
            KeyboardRow row = new KeyboardRow();
            for (String buttonText : rowData) {
                row.add(new KeyboardButton(buttonText));
            }
            keyboard.add(row);
        }
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }
}//перебирает внешний список (rowData — ряды) и внутренний список (buttonText — кнопки) и создает объекты KeyboardRow и KeyboardButton, собирая их в готовый объект ReplyKeyboardMarkup
//принимает входящие сообщения (Update), обрабатывает их, передавая текст в DialogLogic, и отправляет ответные сообщения обратно в тг