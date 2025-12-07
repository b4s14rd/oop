package ru.app.consultation;

import java.util.*;

public class DialogLogic {
    private QuestionRepository questionRepository;
    private boolean isRunning;

    public DialogLogic(QuestionRepository repository) {
        this.questionRepository = repository;
        this.isRunning = true;
    }

    public BotResponse processInput(String input) {
        String responseText;
        List<List<String>> keyboard = List.of(List.of("список", "/help", "выход"));

        if (input.equals("/help") || input.equals("\\help")) {
            responseText = getHelpText();
        } else if (input.equalsIgnoreCase("список")) {
            return getQuestionsListResponse();
        } else if (input.startsWith("вопрос")) {
            responseText = handleQuestionCommand(input);
        } else if (input.equalsIgnoreCase("выход")) {
            isRunning = false;
            responseText = "До свидания! (Бот остановлен)";
            keyboard = List.of();
        } else {
            responseText = "Неизвестная команда. Введите /help для справки.";
        }

        return new BotResponse(responseText, keyboard);
    }

    public boolean isRunning() {
        return isRunning;
    }

    public String getWelcomeMessage() {
        return "<b>Добро пожаловать в систему консультаций по патчингу инструкций!</b>\n" +
                "Введите /help для просмотра доступных команд.";
    }

    public BotResponse getQuestionsListResponse() {
        Map<Integer, Question> questions = questionRepository.getQuestions();
        if (questions.isEmpty()) {
            return new BotResponse("Нет доступных вопросов.");
        }

        StringBuilder sb = new StringBuilder("<b>Доступные вопросы:</b>\n");
        List<List<String>> keyboard = new ArrayList<>();
        List<String> row = new ArrayList<>();

        for (Map.Entry<Integer, Question> entry : questions.entrySet()) {
            sb.append(entry.getKey()).append(". ").append(entry.getValue().getTitle()).append("\n");

            String buttonText = "вопрос " + entry.getKey();
            row.add(buttonText);

            if (row.size() == 2) {
                keyboard.add(row);
                row = new ArrayList<>();
            }
        }
        if (!row.isEmpty()) {
            keyboard.add(row);
        }

        keyboard.add(List.of("/help", "выход"));

        return new BotResponse(sb.toString(), keyboard);
    }

    private String getQuestionsListText() {
        Map<Integer, Question> questions = questionRepository.getQuestions();
        if (questions.isEmpty()) {
            return "Нет доступных вопросов.";
        }

        StringBuilder sb = new StringBuilder("Доступные вопросы:\n");
        for (Map.Entry<Integer, Question> entry : questions.entrySet()) {
            sb.append(entry.getKey()).append(". ").append(entry.getValue().getTitle()).append("\n");
        }
        return sb.toString();
    }

    private String getHelpText() {
        return """
               <b>Я консультационная система по патчингу инструкций.</b>

               <b>Доступные команды:</b>
               /help - показать эту справку
               список - показать все доступные вопросы (кнопками)
               вопрос (номер) - показать вопрос с указанным номером
               выход - завершить работу программы

               Как взаимодействовать:
               1. Введите 'список' чтобы увидеть все вопросы
               2. Введите 'вопрос (номер)' чтобы посмотреть конкретный вопрос
               3. Для выхода введите 'выход'""";
    }

    private String handleQuestionCommand(String command) {
        String[] parts = command.split(" ");
        if (parts.length != 2) {
            return "Использование: вопрос (номер)";
        }

        try {
            int questionNumber = Integer.parseInt(parts[1]);
            Question question = questionRepository.getQuestion(questionNumber);

            if (question != null) {
                String answerContent = question.getAnswer();//экранируем все < и > (включая <адрес>)

                answerContent = answerContent.replace("<", "&lt;").replace(">", "&gt;");

                return "<b>Вопрос:</b> " + question.getTitle() + "\n\n"//оборачиваем безопасный контент в <pre> для сохранения форматирования
                        + "<pre>" + answerContent + "</pre>";

            } else {
                return "Вопрос с номером " + questionNumber + " не найден.\n" + getQuestionsListText();
            }
        } catch (NumberFormatException e) {
            return "Неверный формат номера вопроса. Используйте: вопрос (номер)";
        }
    }
}
//принимает очищенный пользовательский ввод, определяет, какая команда была вызвана (/help, список, вопрос), и генерирует соответствующий ответ
