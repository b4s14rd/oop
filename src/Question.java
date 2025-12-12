public class Question {
    private String title;
    private String answer;

    public Question(String title, String answer) {
        this.title = title;
        this.answer = answer;
    }

    public String getTitle() { return title; }
    public String getAnswer() { return answer; }
}
//хранение структуры одного вопроса и ответа в памяти