import java.util.*;

public class DialogLogic {
    private QuestionRepository questionRepository;
    private boolean isRunning;

    public DialogLogic(QuestionRepository repository) {
        this.questionRepository = repository;
        this.isRunning = true;
    }

    public String processInput(String input) {//обрабатывает команды пользователя
        if (input.equals("\\help")) {
            return getHelpText();
        } else if (input.equalsIgnoreCase("список")) {
            return getQuestionsListText();
        } else if (input.startsWith("вопрос")) {
            return handleQuestionCommand(input);
        } else if (input.equalsIgnoreCase("выход")) {
            isRunning = false;
            return "До свидания!";
        } else {
            return "Неизвестная команда. Введите '\\help' для справки.";
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public String getWelcomeMessage() {//приветствие при старте
        return "Добро пожаловать в систему консультаций по патчингу инструкций!\n" +
                "Введите '\\help' для просмотра доступных команд.";
    }

    public String getQuestionsListText() {//показывает список вопросов
        Map<Integer, Question> questions = questionRepository.getQuestions(); // Использован внешний класс Question
        if (questions.isEmpty()) {
            return "Нет доступных вопросов.";
        }

        StringBuilder sb = new StringBuilder("Доступные вопросы:\n");
        for (Map.Entry<Integer, Question> entry : questions.entrySet()) { // Использован внешний класс Question
            sb.append(entry.getKey()).append(". ").append(entry.getValue().getTitle()).append("\n");
        }
        return sb.toString();
    }

    private String getHelpText() {//справка по командам (Text Block)
        return """
               Я консультационная система по патчингу инструкций.

               Доступные команды:
               \\help - показать эту справку
               список - показать все доступные вопросы
               вопрос <номер> - показать вопрос с указанным номером
               выход - завершить работу программы

               Как взаимодействовать:
               1. Введите 'список' чтобы увидеть все вопросы
               2. Введите 'вопрос <номер>' чтобы посмотреть конкретный вопрос
               3. В любой момент можно ввести '\\help' для справки
               4. Для выхода введите 'выход'""";
    }

    private String handleQuestionCommand(String command) {//обрабатывает команду "вопрос"
        String[] parts = command.split(" ");
        if (parts.length != 2) {
            return "Использование: вопрос <номер>";
        }

        try {
            int questionNumber = Integer.parseInt(parts[1]);
            Question question = questionRepository.getQuestion(questionNumber); // Использован внешний класс Question

            if (question != null) {
                return "Вопрос: " + question.getTitle() + "\n\n" + question.getAnswer();
            } else {
                return "Вопрос с номером " + questionNumber + " не найден.\n" + getQuestionsListText();
            }
        } catch (NumberFormatException e) {
            return "Неверный формат номера вопроса. Используйте: вопрос <номер>";
        }
    }
}
