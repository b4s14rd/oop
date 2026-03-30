import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GoogleSheetsExporter {
    private static final String SPREADSHEET_ID = System.getenv("SPREADSHEET_ID");
    private static final String CREDENTIALS_FILE_PATH = System.getenv("GOOGLE_CREDENTIALS_PATH");

    public static void exportData(ApplicationForm form) {
        try {//чтение ключа авторизации, те открываем файл и запрашиваем права на работу с таблицами
            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(CREDENTIALS_FILE_PATH))
                    .createScoped(Collections.singleton("https://www.googleapis.com/auth/spreadsheets"));

            Sheets service = new Sheets.Builder(//инициализация сервиса -> создаем объект Sheets, через который будем слать данные
                    GoogleNetHttpTransport.newTrustedTransport(),//создает защищенное соединение
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials))//«подкладывает» наш ключ в каждый запрос "(название .json)"
                    .setApplicationName("TelegramBotCollector")
                    .build();

            List<Object> rowData = Arrays.asList(//список данных
                    new java.util.Date().toString(),
                    "Tg-Bot",
                    "@" + form.getTelegramUsername(),
                    form.getName(),
                    form.getEmail(),
                    form.getGoal(),
                    form.getCity()
            );

            ValueRange body = new ValueRange().setValues(Collections.singletonList(rowData));//очень жаль, что Google API принимает только объекты типа ValueRange

            service.spreadsheets().values()
                    .append(SPREADSHEET_ID, "Лист1!A1", body)//ищет первую пустую строку ниже Лист1!A1 и записывает данные
                    .setValueInputOption("RAW")//данные нужно вставлять как текст, не пытаясь их форматировать
                    .execute();

            System.out.println("Данные успешно добавлены с источником!");

        } catch (IOException | GeneralSecurityException e) {//ошибки сети, доступа или отсутствия файла ключа
            System.err.println("Ошибка API: " + e.getMessage());
            e.printStackTrace();
        }
    }
}