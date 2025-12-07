package ru.app.consultation;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class QuestionRepository {
    private Map<Integer, Question> questions;
    private String dataFile;

    public QuestionRepository(String dataFilePath) {
        this.dataFile = dataFilePath;
        this.questions = loadDataFromFile();
    }

    private Map<Integer, Question> loadDataFromFile() {
        Map<Integer, Question> loadedQuestions = new HashMap<>();

        try {
            File file = new File(dataFile);

            if (!file.exists()) {
                createDefaultDataFile();
            }

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

    private void createDefaultDataFile() {
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
    }

    public Question getQuestion(int id) {
        return questions.get(id);
    }
}
//загружает вопросы и ответы из текстового файла (instruction_patching_data.txt) в оперативную память и предоставляет методы для их получения
