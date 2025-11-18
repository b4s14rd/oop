import java.io.*;
import java.nio.file.*;
import java.util.*;

public class QuestionRepository {
    private Map<Integer, Question> questions;
    private String dataFile;

    public QuestionRepository(String dataFilePath) {//загружает вопросы из файла
        this.dataFile = dataFilePath;
        this.questions = new HashMap<>();
        loadDataFromFile();
    }

    private void loadDataFromFile() {//читает файл с вопросами
        try {
            File file = new File(dataFile);

            if (!file.exists()) {
                createDefaultDataFile();
                return;
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
                        questions.put(id, new Question(title, answer));
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка формата ID в строке: " + line);
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Ошибка загрузки данных из файла: " + e.getMessage());
        }
    }

    private void createDefaultDataFile() {//создает файл если его нет
        try {
            String defaultContent =
                    "1|Что такое патчинг инструкций?|Патчинг инструкций - это модификация машинного кода программы во время выполнения.\n\n" +
                            "2|Какие основные методы патчинга?|Основные методы:\n- Замена инструкций\n- NOP-заполнение\n- Хот-патч\n- Хук-функции\n\n" +
                            "3|Для чего используется патчинг?|Патчинг используется для:\n- Отладки\n- Модификации поведения\n- Исправления багов\n- Анализа безопасности";

            Files.write(Paths.get(dataFile), defaultContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            loadDataFromFile();

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

    public static class Question {
        private String title;
        private String answer;

        public Question(String title, String answer) {
            this.title = title;
            this.answer = answer;
        }

        public String getTitle() { return title; }
        public String getAnswer() { return answer; }
    }
}
