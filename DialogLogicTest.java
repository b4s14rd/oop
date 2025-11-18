public class DialogLogicTest {

    public static void main(String[] args) {
        testHelpCommand();
        testQuestionCommands();
        testExitCommand();
    }

    public static void testHelpCommand() {//тест 1 проверка \help
        QuestionRepository repository = new QuestionRepository("test_data.txt");
        DialogLogic logic = new DialogLogic(repository);

        String response = logic.processInput("\\help");

        System.out.println("Тест 1: Команда \\help : ");
        System.out.println("Ввод: \\help");
        System.out.println("Вывод бота:");
        System.out.println(response);
        System.out.println();

        if (response.contains("Доступные команды") && response.contains("\\help")) {
            System.out.println("Тест пройден - \\help работает правильно");
        } else {
            System.out.println("Тест не пройден");
        }
        System.out.println();
    }

    public static void testQuestionCommands() {//тест 2 правильные неправильные вопросы
        QuestionRepository repository = new QuestionRepository("test_data.txt");
        DialogLogic logic = new DialogLogic(repository);

        System.out.println("Тест 2: Команды вопросов : ");

        System.out.println("Ввод: вопрос 1");//правильная
        String validResponse = logic.processInput("вопрос 1");
        System.out.println("Вывод бота:");
        System.out.println(validResponse);
        System.out.println();

        System.out.println("Ввод: вопрос 999");//неправильная
        String invalidResponse = logic.processInput("вопрос 999");
        System.out.println("Вывод бота:");
        System.out.println(invalidResponse);
        System.out.println();

        if (validResponse.contains("Вопрос:") && invalidResponse.contains("не найден")) {
            System.out.println("Тест пройден - команды вопросов работают правильно");
        } else {
            System.out.println("Тест не пройден");
        }
        System.out.println();
    }

    public static void testExitCommand() {//проверка работы кода
        QuestionRepository repository = new QuestionRepository("test_data.txt");
        DialogLogic logic = new DialogLogic(repository);

        System.out.println("Тест 3: Работа кода : ");

        System.out.println("Ввод: unknown");//неизвестная команда
        String unknownResponse = logic.processInput("unknown");
        System.out.println("Вывод бота:");
        System.out.println(unknownResponse);
        System.out.println();

        System.out.println("Ввод: выход");//команда выхода
        String exitResponse = logic.processInput("выход");
        System.out.println("Вывод бота:");
        System.out.println(exitResponse);

        boolean wasRunning = logic.isRunning();
        System.out.println("Программа работает после выхода: " + wasRunning);

        if (unknownResponse.contains("Неизвестная команда") && !wasRunning) {
            System.out.println("Тест пройден - код работает правильно, ураураураура");
        } else {
            System.out.println("Тест не пройден");
        }
    }
}
