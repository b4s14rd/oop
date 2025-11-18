import java.util.*;

public class DialogLogic {
    private QuestionRepository questionRepository;
    private boolean isRunning;

    public DialogLogic(QuestionRepository repository) {
        this.questionRepository = repository;
        this.isRunning = true;
    }

    public String processInput(String input) {
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

    public String getWelcomeMessage() {
        return "Добро пожаловать в систему консультаций по патчингу инструкций!\n" +
                "Введите '\\help' для просмотра доступных команд.";
    }

    public String getQuestionsListText() {
        Map<Integer, QuestionRepository.Question> questions = questionRepository.getQuestions();
        if (questions.isEmpty()) {
            return "Нет доступных вопросов.";
        }

        StringBuilder sb = new StringBuilder("Доступные вопросы:\n");
        for (Map.Entry<Integer, QuestionRepository.Question> entry : questions.entrySet()) {
            sb.append(entry.getKey()).append(". ").append(entry.getValue().getTitle()).append("\n");
        }
        return sb.toString();
    }

    private String getHelpText() {
        return "Я консультационная система по патчингу инструкций. \n\n" +
                "Доступные команды:\n" +
                "\\help - показать эту справку\n" +
                "список - показать все доступные вопросы\n" +
                "вопрос <номер> - показать вопрос с указанным номером\n" +
                "выход - завершить работу программы\n\n" +
                "Как взаимодействовать:\n" +
                "1. Введите 'список' чтобы увидеть все вопросы\n" +
                "2. Введите 'вопрос <номер>' чтобы посмотреть конкретный вопрос\n" +
                "3. В любой момент можно ввести '\\help' для справки\n" +
                "4. Для выхода введите 'выход'";
    }

    private String handleQuestionCommand(String command) {
        String[] parts = command.split(" ");
        if (parts.length != 2) {
            return "Использование: вопрос <номер>";
        }

        try {
            int questionNumber = Integer.parseInt(parts[1]);
            QuestionRepository.Question question = questionRepository.getQuestion(questionNumber);

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
