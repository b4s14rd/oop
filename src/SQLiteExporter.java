import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SQLiteExporter {
    private static final String DATABASE_URL = "jdbc:sqlite:leads.db";//указывает драйверу SQLite, где именно создать файл базы (в корне проекта)
    private static final String TABLE_NAME = "Заявки";//название таблицы в базе

    public static void initialize() {//метод создает файл базы данных и структуру таблицы
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement()) {//нужен для отправки простых текстовых команд в базу

            String sql = "CREATE TABLE IF NOT EXISTS \"" + TABLE_NAME + "\" ("//SQL-инструкция: создаем таблицу, если её еще нет и используем \", чтобы скл корректно воспринял кириллицу и пробелы
                    + "\"№\" INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "\"Дата и время\" TEXT NOT NULL,"
                    + "\"Telegram ID\" TEXT NOT NULL,"
                    + "\"Имя\" TEXT,"
                    + "\"Почта\" TEXT,"
                    + "\"Цель\" TEXT"
                    + ");";

            stmt.execute(sql);//отправляет написанный выше SQL-код на выполнение в базу
            System.out.println("База данных SQLite готова. Таблица: " + TABLE_NAME);

        } catch (SQLException e) {
            System.err.println("Ошибка при инициализации базы: " + e.getMessage());
        }
    }

    public static void exportData(ApplicationForm form) {//метод для записи данных анкеты в базу
        //используем ?, чтобы защититься от ошибок и "инъекций". Это типа шаблонов для данных.
        String sql = "INSERT INTO \"" + TABLE_NAME + "\" " +
                "(\"Дата и время\", \"Telegram ID\", \"Имя\", \"Почта\", \"Цель\") " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//более безопасный способ вставки данных, чем обычный Statement

            pstmt.setString(1, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            pstmt.setString(2, "@" + form.getTelegramUsername());
            pstmt.setString(3, form.getName());//заполняем каждый ? реальными данными из объекта анкеты (form)
            pstmt.setString(4, form.getEmail());
            pstmt.setString(5, form.getGoal());

            pstmt.executeUpdate();//совершает фактическую запись в файл leads.db
            System.out.println("Заявка успешно сохранена в базу данных");

        } catch (SQLException e) {
            System.err.println("Ошибка при записи в базу: " + e.getMessage());
            e.printStackTrace();
        }
    }
}