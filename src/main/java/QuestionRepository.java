import java.io.*;
import java.nio.file.*;
import java.util.*;

public class QuestionRepository {//при создании объекта QuestionRepository он сразу вызывает метод loadDataFromFile(), таким образом данные загружаются в память только один раз при старте бота
    //основная структура данных, хранит все вопросы где ключ (Integer) это номер вопроса (id), а значение (Question) это объект, содержащий заголовок и ответ
    private Map<Integer, Question> questions;
    private String dataFile;

    public QuestionRepository(String dataFilePath) {
        this.dataFile = dataFilePath;//путь к файлу, из которого нужно загружать данные ("instruction_patching_data.txt")
        this.questions = loadDataFromFile();
    }

    private Map<Integer, Question> loadDataFromFile() {
        Map<Integer, Question> loadedQuestions = new HashMap<>();

        try {
            File file = new File(dataFile);

            if (!file.exists()) {//если файла нет, он пытается его создать с дефолтным содержимым, гарантия запуска бота
                createDefaultDataFile();
            }

            if (!file.exists()) {
                return loadedQuestions;
            }

            List<String> lines = Files.readAllLines(file.toPath(), java.nio.charset.StandardCharsets.UTF_8);//читает весь файл сразу построчно

            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\|", 3);//разбивает каждую строку на части, разделитель — вертикальная черта
                if (parts.length == 3) {
                    try {
                        int id = Integer.parseInt(parts[0].trim());//преобразует первую часть строки в число, обернуто в try-catch (NumberFormatException) чтобы избежать ерроров, если id окажется не числом
                        String title = parts[1].trim();
                        String answer = parts[2].trim();
                        answer = answer.replace("\\n", "\n");//заменяет текстовую последовательность \n (которую мы юзаем в файле данных для переноса строки) на настоящий символ переноса строки
                        loadedQuestions.put(id, new Question(title, answer));
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка формата ID в строке: " + line);
                    }
                }
            }

            return loadedQuestions;

        } catch (IOException e) {//тяжело, оч тяжело
            System.out.println("Ошибка загрузки данных из файла: " + e.getMessage());
            return loadedQuestions;
        }
    }

    private void createDefaultDataFile() {//резервное копирование на всякий
        try {
            String defaultContent =
                    "1|Что такое патчинг инструкций?|Патчинг инструкций - это модификация машинного кода программы во время выполнения.\n\n" +
                            "2|Какие основные методы патчинга?|Основные методы:\n- Замена инструкций\n- NOP-заполнение\n- Хот-патч\n- Хук-функции\n\n" +
                            "3|Для чего используется патчинг?|Патчинг используется для:\n- Отладки\n- Модификации поведения\n- Исправления багов\n- Анализа безопасности";

            Files.write(Paths.get(dataFile), defaultContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("Ошибка создания файла: " + e.getMessage());
        }
    }

    public Map<Integer, Question> getQuestions() {
        return new HashMap<>(questions);
    }//возвращает копию (new HashMap<>())

    public Question getQuestion(int id) {
        return questions.get(id);
    }//возвращает конкретный объект
}
//загружает вопросы и ответы из текстового файла (instruction_patching_data.txt) в оперативную память и предоставляет методы для их получения