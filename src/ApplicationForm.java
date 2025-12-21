public class ApplicationForm {
    private String telegramUsername;
    private String name;
    private String email;
    private String city;
    private String goal;
    private int step;//шаг заполнения анкеты

    public ApplicationForm(String telegramUsername) {
        this.telegramUsername = telegramUsername;
        this.step = 1; //начинаем с первого шага
    }

    public String getCurrentQuestion() {
        switch (step) {//смотрим на каком мы шаге, и записываем ответ в нужное поле
            case 1: return "Введите ваше <b>Имя</b>:";
            case 2: return "Введите ваш <b>Email</b>:";
            case 3: return "Из какого вы <b>Города</b>?";
            case 4: return "Какова ваша <b>Цель</b> обучения?";
            default: return "Анкета завершена.";
        }
    }

    public String processAnswer(String answer) {
        switch (step) {
            case 1:
                this.name = answer.trim();//убираем лишние пробелы в начале/конце
                step++;//переходим к следующему шагу
                return getCurrentQuestion();//текст для следующего вопроса
            case 2:
                this.email = answer.trim();
                step++;
                return getCurrentQuestion();
            case 3:
                this.city = answer.trim();
                step++;
                return getCurrentQuestion();
            case 4:
                this.goal = answer.trim();
                step++;
                return "Спасибо, <b>" + name + "</b>! Заявка принята. Данные сохранены в систему.";//финальное сообщение
            default:
                return "Анкета уже заполнена.";
        }
    }

    public boolean isCompleted() {//метод проверяет если шаг больше 4, значит все вопросы эта, все хорошо с ними, на все ответили
        return step > 4;
    }

    public String getTelegramUsername() { return telegramUsername; }
    public String getCity() { return city; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getGoal() { return goal; }
}