import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class GoogleSheetsExporter {
    private static final String SCRIPT_URL = System.getenv("GOOGLE_SCRIPT_URL");
    private static final String BOT_SOURCE = "Tg-Bot";

    public static void exportData(ApplicationForm form) {
        String json = String.format(
                "{\"source\":\"%s\", \"tgId\":\"%s\", \"name\":\"%s\", \"email\":\"%s\", \"city\":\"%s\", \"goal\":\"%s\"}",
                BOT_SOURCE,
                "@" + form.getTelegramUsername(),
                form.getName(),
                form.getEmail(),
                form.getCity(),
                form.getGoal()
        );

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)//если нужно идти по другому адресу, то идет до финальной точки
                .build();

        HttpRequest request = HttpRequest.newBuilder()//создание пост запроса
                .uri(URI.create(SCRIPT_URL))
                .header("Content-Type", "application/json")//говорим гуглу, что шлем жсон
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())//асинхронная отправка (бот продолжает работать)
                .thenAccept(res -> System.out.println("Данные ушли в Google. Статус: " + res.statusCode()))
                .exceptionally(ex -> {
                    System.err.println("Ошибка отправки: " + ex.getMessage());
                    return null;
                });
    }
}