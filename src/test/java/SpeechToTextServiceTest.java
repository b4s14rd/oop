import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class SpeechToTextServiceTest {

    private SpeechToTextService sttService;

    @BeforeEach
    void setUp() {
        sttService = new SpeechToTextService();
    }

    @Test
    void testTranscribeWithMissingToken() {
        String originalToken = System.getenv("HF_API_TOKEN");
        assumeTrue(originalToken == null || originalToken.trim().isEmpty());

        File dummyFile = new File("non_existent_file.mp3");
        String result = sttService.transcribe(dummyFile);

        assertEquals("Ошибка: Токен HF_API_TOKEN не найден в системе.", result);
    }

    @Test
    void testTranscribeRealAudio() {
        String token = System.getenv("HF_API_TOKEN");
        assumeTrue(token != null && !token.trim().isEmpty());

        File audioFile = new File("src/test/java/test_voice.mp3");
        if (!audioFile.exists()) {
            audioFile = new File("test/java/test_voice.mp3");
        }
        if (!audioFile.exists()) {
            audioFile = new File("test_voice.mp3");
        }

        assertTrue(audioFile.exists(), "Файл test_voice.mp3 не найден ни по одному из стандартных путей.");

        String result = sttService.transcribe(audioFile);
        System.out.println("Распознанный текст из аудио: " + result);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertFalse(result.contains("Ошибка"));
    }
}