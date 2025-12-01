public class Main {//
    public static void main(String[] args) {
        String dataFilePath = "instruction_patching_data.txt";

        QuestionRepository repository = new QuestionRepository(dataFilePath);
        DialogLogic logic = new DialogLogic(repository);
        ConsoleInterface console = new ConsoleInterface(logic);

        console.startInteraction(); //взаимодействие
    }
}
