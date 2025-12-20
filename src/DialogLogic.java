import java.util.*;

public class DialogLogic {
    private QuestionRepository questionRepository;
    private boolean isRunning;//флаг который используется для проверки состояния бота
    private Map<Long, ApplicationForm> activeForms = new HashMap<>();//хранилище активных анкет. Ключ - Chat ID пользователя (Map хранит активные анкеты: ключ - ID чата, значение - объект с ответами этого юзера)

    public DialogLogic(QuestionRepository repository) {
        this.questionRepository = repository;
        this.isRunning = true;
    }

    //метод принимает chatId и username для работы с анкетами
    public BotResponse processInput(String input, long chatId, String username) {//проверяет что написали в тг по типу /help, список и тд, возвращает ответ
        String responseText;
        List<List<String>> keyboard = List.of(List.of("список", "/help", "курс", "выход"));

        if (activeForms.containsKey(chatId)) {//если пользователь в процессе анкеты, обрабатываем его ввод отдельно
            return handleApplicationInput(input, chatId);
        }

        if (input.equals("/help") || input.equals("\\help")) {
            responseText = getHelpText();
        } else if (input.equalsIgnoreCase("список")) {
            return getQuestionsListResponse();
        } else if (input.equalsIgnoreCase("курс")) {
            return askForCourseEnrollment();//пользователь нажал кнопку "курс"
        } else if (input.equalsIgnoreCase("Да, хочу!") || input.equalsIgnoreCase("Нет, спасибо.")) {
            return handleEnrollmentAnswer(input, chatId, username);//обработка согласия на запись
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

    private BotResponse handleApplicationInput(String input, long chatId) {//пошаговая обработка ответов внутри анкеты
        if (input.equalsIgnoreCase("отмена")) {//если юзер написал "отмена", мы удаляем его анкету из памяти (Map)
            activeForms.remove(chatId);
            return new BotResponse("Заполнение анкеты отменено.", List.of(List.of("список", "курс")));
        }

        ApplicationForm form = activeForms.get(chatId);//достаем анкету конкретного пользователя из памяти по его chatId
        String responseText = form.processAnswer(input);//передаем ввод юзера в анкету, она сама поймет, на какой вопрос это ответ

        if (form.isCompleted()) {//проверяем, ответил ли юзер на все вопросы (шаг 4 и >)
            SQLiteExporter.exportData(form);//когда анкета готова, сохраняем её в SQLite
            activeForms.remove(chatId);//удаляем анкету из памяти, чтобы юзер мог начать новую позже
            return new BotResponse(responseText, List.of(List.of("список", "курс", "выход")));
        }

        return new BotResponse(responseText, List.of(List.of("отмена")));//возвращаем текст следующего вопроса или сообщение об успехе
    }

    public BotResponse askForCourseEnrollment() {//предложение записаться на курс
        return new BotResponse("Хотите записаться на наш курс по патчингу?",
                List.of(List.of("Да, хочу!", "Нет, спасибо.")));
    }

    private BotResponse handleEnrollmentAnswer(String input, long chatId, String username) {//создание новой анкеты при согласии
        if (input.equalsIgnoreCase("Да, хочу!")) {//если юзер нажал "Да, хочу!", создаем для него новый объект анкеты
            ApplicationForm newForm = new ApplicationForm(username);
            activeForms.put(chatId, newForm);//кладем анкету в Map, чтобы при следующем сообщении бот знал, что идет опрос
            return new BotResponse(newForm.getCurrentQuestion(), List.of(List.of("отмена")));//задаем первый вопрос из анкеты
        }
        return new BotResponse("Хорошо, если передумаете — кнопка 'курс' всегда под рукой.",//если отказался просто возвращаем стандартные кнопки
                List.of(List.of("список", "курс")));
    }

    public boolean isRunning() {
        return isRunning;
    }

    public String getWelcomeMessage() {//привет привет
        return "<b>Добро пожаловать в систему консультаций по патчингу инструкций!</b>\n" +
                "Введите /help для просмотра доступных команд.";
    }

    public BotResponse getQuestionsListResponse() {//формируем список тем по которым получаем консультацию
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

            if (row.size() == 2) {//чтобы кнопки не стояли в один ряд делим их по 2 в строку
                keyboard.add(row);
                row = new ArrayList<>();
            }
        }
        if (!row.isEmpty()) {
            keyboard.add(row);
        }

        keyboard.add(List.of("/help", "курс", "выход"));

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

    private String getHelpText() {//прост текст помощи
        return """
               <b>Я консультационная система по патчингу инструкций.</b>

               <b>Доступные команды:</b>
               /help - показать эту справку
               список - показать все доступные вопросы (кнопками)
               вопрос (номер) - показать вопрос с указанным номером
               курс - записаться на обучение
               выход - завершить работу программы""";
    }

    private String handleQuestionCommand(String command) {//обрабатывает запрос типа "вопрос 1"
        String[] parts = command.split(" ");//разделяет строку по пробелу чтобы получить цифру (ака id вопроса)
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
                        + "<pre>" + answerContent + "</pre>";//(доп) оборачиваем в <pre> чтобы внутри тг текст выглядел нормально со всеми отступами

            } else {
                return "Вопрос с номером " + questionNumber + " не найден.\n" + getQuestionsListText();
            }
        } catch (NumberFormatException e) {
            return "Неверный формат номера вопроса. Используйте: вопрос (номер)";
        }
    }
}
//принимает очищенный пользовательский ввод, определяет, какая команда была вызвана (/help, список, вопрос), и генерирует соответствующий ответ