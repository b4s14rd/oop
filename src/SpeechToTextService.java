import java.io.File;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.Duration;

public class SpeechToTextService {
    private static final String HF_API_TOKEN = System.getenv("HF_API_TOKEN");
    private static final String API_URL = "https://router.huggingface.co/hf-inference/models/openai/whisper-large-v3";

    private final HttpClient client;

    public SpeechToTextService() {
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .proxy(ProxySelector.of(new InetSocketAddress("127.0.0.1", 10808)))
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    public String transcribe(File audioFile) {
        if (HF_API_TOKEN == null || HF_API_TOKEN.trim().isEmpty()) {
            return "Ошибка: Токен HF_API_TOKEN не найден в системе.";
        }

        try {
            byte[] audioData = Files.readAllBytes(audioFile.toPath());
            String cleanToken = HF_API_TOKEN.trim();

            String contentType = "audio/ogg";
            if (audioFile.getName().endsWith(".mp3")) contentType = "audio/mpeg";
            if (audioFile.getName().endsWith(".wav")) contentType = "audio/wav";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("User-Agent", "curl/8.18.0") // Наш "пропуск" через роутер
                    .header("Authorization", "Bearer " + cleanToken)
                    .header("Accept", "application/json")
                    .header("Content-Type", contentType)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(audioData))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int status = response.statusCode();
            String body = response.body();

            if (status == 200) {
                String result = parseTextFromJson(body);
                return result.isEmpty() ? "Голосовое сообщение пустое или не распознано." : result;
            } else if (status == 503) {
                return "Модель загружается. Повторите запрос через 20 секунд.";
            } else {
                System.err.println("[DEBUG] STT Error Body: " + body);
                return "Ошибка API (" + status + ")";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка сервиса: " + e.getMessage();
        }
    }

    private String parseTextFromJson(String json) {
        try {
            if (json.contains("\"text\":\"")) {
                String result = json.split("\"text\":\"")[1];
                result = result.substring(0, result.lastIndexOf("\""));
                return result.trim();
            }
        } catch (Exception e) {
            return "Ошибка парсинга JSON";
        }
        return "";
    }
}