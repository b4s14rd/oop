import java.util.Scanner;

public class ConsoleInterface {
    private DialogLogic dialogLogic;
    private Scanner scanner;

    public ConsoleInterface(DialogLogic logic) {
        this.dialogLogic = logic;//
        this.scanner = new Scanner(System.in);
    }

    public void startInteraction() {
        System.out.println(dialogLogic.getWelcomeMessage());//прив прив

        System.out.println("\n" + dialogLogic.getQuestionsListText());//список вопросов сразу

        while (dialogLogic.isRunning()) {//бесконечный цикл диалога
            System.out.print("\n> ");
            String userInput = scanner.nextLine().trim();

            if (!userInput.isEmpty()) {
                String response = dialogLogic.processInput(userInput);
                System.out.println(response);
            }
        }

        scanner.close();
    }
}
