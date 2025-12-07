package ru.app.consultation;

import java.util.List;

public class BotResponse {
    private final String text;
    private final List<List<String>> keyboardButtons;

    public BotResponse(String text) {
        this(text, List.of());
    }

    public BotResponse(String text, List<List<String>> keyboardButtons) {
        this.text = text;
        this.keyboardButtons = keyboardButtons;
    }

    public String getText() { return text; }
    public List<List<String>> getKeyboardButtons() { return keyboardButtons; }
}
//кпаковывает текст ответа и информацию о кнопках в один удобный объект
