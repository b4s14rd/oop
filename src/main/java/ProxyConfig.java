import org.telegram.telegrambots.bots.DefaultBotOptions;
import java.io.FileInputStream;
import java.util.Properties;

public class ProxyConfig {
    public static DefaultBotOptions getOptions() {
        DefaultBotOptions options = new DefaultBotOptions();
        Properties props = new Properties();

        try (FileInputStream fis = new FileInputStream("proxy.properties")) {
            props.load(fis);

            options.setProxyHost(props.getProperty("proxy.host"));
            options.setProxyPort(Integer.parseInt(props.getProperty("proxy.port")));

            String type = props.getProperty("proxy.type");
            if ("SOCKS5".equalsIgnoreCase(type)) {
                options.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);
            } else {
                options.setProxyType(DefaultBotOptions.ProxyType.HTTP);
            }

            System.out.println("Прокси сконфигурирован успешно.");
        } catch (Exception e) {
            System.out.println("Файл proxy.properties не найден или пуст. Бот запустится без прокси.");
        }
        return options;
    }
}