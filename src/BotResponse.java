import java.util.List;

public class BotResponse {
    private final String text;//текст ответа, который увидит пользователь, сюда включается вся HTML-разметка
    private final List<List<String>> keyboardButtons;

    public BotResponse(String text) {//основной конструктор, который используется когда ты хочешь отправить и текст и кнопки
        this(text, List.of());
    }

    public BotResponse(String text, List<List<String>> keyboardButtons) {//конструктор позволяет создать объект, передав только текст, он автоматически вызывает основной конструктор, передавая в качестве клавиатуры пустой список (List.of()), это используется когда нужно отправить простое текстовое сообщение без кнопок
        this.text = text;
        this.keyboardButtons = keyboardButtons;//структура кнопок, это список строк (внешний List), а каждая строка содержит список текстов кнопок (внутренний List)
    }

    public String getText() { return text; }
    public List<List<String>> getKeyboardButtons() { return keyboardButtons; }//геттеры
}
//кпаковывает текст ответа и информацию о кнопках в один удобный объект