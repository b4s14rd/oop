import java.io.*;//
import java.nio.file.*;
import java.util.*;

public class QuestionRepository {
    private Map<Integer, Question> questions;
    private String dataFile;

    public QuestionRepository(String dataFilePath) {//загружает вопросы из файла
        this.dataFile = dataFilePath;
        this.questions = loadDataFromFile(); // Инициализируем поле результатом метода
    }

    private Map<Integer, Question> loadDataFromFile() {//читает файл с вопросами и возвращает Map
        Map<Integer, Question> loadedQuestions = new HashMap<>(); // Локальная переменная

        try {
            File file = new File(dataFile);

            if (!file.exists()) {
                createDefaultDataFile();
            }

            // Если файл все еще не существует после попытки создания, возвращаем пустой Map
            if (!file.exists()) {
                return loadedQuestions;
            }

            List<String> lines = Files.readAllLines(file.toPath(), java.nio.charset.StandardCharsets.UTF_8);

            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\|", 3);
                if (parts.length == 3) {
                    try {
                        int id = Integer.parseInt(parts[0].trim());
                        String title = parts[1].trim();
                        String answer = parts[2].trim();
                        answer = answer.replace("\\n", "\n");
                        loadedQuestions.put(id, new Question(title, answer)); // Заполняем локальную Map
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка формата ID в строке: " + line);
                    }
                }
            }

            return loadedQuestions; // Возвращаем собранные данные

        } catch (IOException e) {
            System.out.println("Ошибка загрузки данных из файла: " + e.getMessage());
            return loadedQuestions;
        }
    }

    private void createDefaultDataFile() {//создает файл если его нет
        try {
            String defaultContent =
                    "1|Что такое патчинг инструкций?|Патчинг инструкций - это модификация машинного кода программы во время выполнения.\n\n" +
                            "2|Какие основные методы патчинга?|Основные методы:\n- Замена инструкций\n- NOP-заполнение\n- Хот-патч\n- Хук-функции\n\n" +
                            "3|Для чего используется патчинг?|Патчинг используется для:\n- Отладки\n- Модификации поведения\n- Исправления багов\n- Анализа безопасности";

            Files.write(Paths.get(dataFile), defaultContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            // Убран вызов loadDataFromFile(), так как его выполнит конструктор
        } catch (IOException e) {
            System.out.println("Ошибка создания файла: " + e.getMessage());
        }
    }

    public Map<Integer, Question> getQuestions() {
        return new HashMap<>(questions);
    }//возвращает все вопросы

    public Question getQuestion(int id) {
        return questions.get(id);
    }//возвращает вопрос по номеру

    // Внутренний класс Question УДАЛЕН.
}
