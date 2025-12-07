package ru.app.consultation;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

//основной класс тг бота, обрабатывающий входящие сообщения и использующий DialogLogic для получения ответов
public class TelegramConsultationBot extends TelegramLongPollingBot {

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
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {//проверяем, что это сообщение, и что оно содержит текст
            String userInput = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (userInput.equalsIgnoreCase("/start")) {//отправляем приветственное сообщение и список вопросов при старте
                sendResponse(chatId, new BotResponse(dialogLogic.getWelcomeMessage()));
                sendResponse(chatId, dialogLogic.getQuestionsListResponse());
            } else {//обрабатываем ввод через DialogLogic
                BotResponse botResponse = dialogLogic.processInput(userInput);
                sendResponse(chatId, botResponse);
            }
        }
    }

    private void sendResponse(long chatId, BotResponse response) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(response.getText());
        message.setParseMode("HTML"); //включаем HTML разметку для жирного шрифта

        if (!response.getKeyboardButtons().isEmpty()) {
            message.setReplyMarkup(createKeyboard(response.getKeyboardButtons()));
        }

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private ReplyKeyboardMarkup createKeyboard(List<List<String>> buttonData) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

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
}
//принимает входящие сообщения (Update), обрабатывает их, передавая текст в DialogLogic, и отправляет ответные сообщения обратно в тг
