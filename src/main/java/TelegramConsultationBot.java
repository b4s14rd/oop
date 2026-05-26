import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.bots.DefaultBotOptions;

import java.util.ArrayList;
import java.util.List;

public class TelegramConsultationBot extends TelegramLongPollingBot {//основной класс тг бота, обрабатывающий входящие сообщения и использующий DialogLogic для получения ответов

    private final DialogLogic dialogLogic;
    private final SpeechToTextService sttService = new SpeechToTextService();
    private static final String BOT_TOKEN = System.getenv("BOT_TOKEN");
    private static final String BOT_USERNAME = System.getenv("BOT_USERNAME");

    public TelegramConsultationBot(DialogLogic dialogLogic, DefaultBotOptions options) {
        super(options);
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
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) return;

        long chatId = update.getMessage().getChatId();
        String username = update.getMessage().getFrom().getUserName();
        if (username == null) username = String.valueOf(chatId);

        if (update.getMessage().hasText()) {
            String userInput = update.getMessage().getText();

            if (userInput.equalsIgnoreCase("/start")) {
                sendResponse(chatId, new BotResponse(dialogLogic.getWelcomeMessage()));
                sendResponse(chatId, dialogLogic.askForCourseEnrollment());
            } else {
                handleInput(userInput, chatId, username);
            }
        }
        else if (update.getMessage().hasVoice()) {
            sendResponse(chatId, new BotResponse("<i>Расшифровываю ваше сообщение...</i>"));

            String recognizedText = processVoiceMessage(update.getMessage().getVoice());
            handleInput(recognizedText, chatId, username);
        }
    }

    private void handleInput(String text, long chatId, String username) {
        BotResponse botResponse = dialogLogic.processInput(text, chatId, username);
        sendResponse(chatId, botResponse);
    }

    private String processVoiceMessage(org.telegram.telegrambots.meta.api.objects.Voice voice) {
        try {
            org.telegram.telegrambots.meta.api.methods.GetFile getFileMethod = new org.telegram.telegrambots.meta.api.methods.GetFile();
            getFileMethod.setFileId(voice.getFileId());
            org.telegram.telegrambots.meta.api.objects.File file = execute(getFileMethod);

            java.io.File tempAudioFile = downloadFile(file);

            String transcription = sttService.transcribe(tempAudioFile);

            tempAudioFile.delete();

            return transcription;

        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при обработке голосового сообщения.";
        }
    }

    private void sendResponse(long chatId, BotResponse response) {//метод отвечает за преобразование внутреннего объекта BotResponse в формат, понятный тг
        SendMessage message = new SendMessage();
        message.setChatId(chatId);//создается объект SendMessage с нужным chatId (кому отправлять) и текстом
        message.setText(response.getText());
        message.setParseMode("HTML");//включаем HTML разметку для жирного шрифта

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