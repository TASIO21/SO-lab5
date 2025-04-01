import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WindowsAutostart {
    // Название приложения для записи в реестр
    private static final String APP_NAME = "MyJavaAutostartApp";

    public static void main(String[] args) {
        // Проверяем, является ли это первым запуском
        boolean isFirstRun = args.length == 0;

        if (isFirstRun) {
            System.out.println("Первый запуск. Добавление в автозапуск...");
            // Добавляем программу в автозапуск
            if (addToStartup()) {
                // Перезагружаем компьютер после успешного добавления
                restartComputer();
            }
        } else {
            // Если программа запущена из автозапуска, создаем лог-файл
            createLogFile();
            System.out.println("Приложение запущено из автозапуска...");
        }
    }

    /**
     * Добавляет текущую программу в автозапуск Windows через реестр
     */
    private static boolean addToStartup() {
        try {
            // Получаем путь к JAR-файлу текущей программы
            String jarPath = new File(WindowsAutostart.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI()).getAbsolutePath();

            // Исправляем возможные ошибки с разделителями в пути
            jarPath = jarPath.replace("/", "\\");

            // Формируем команду для добавления записи в реестр
            String command = String.format(
                    "reg add HKCU\\SOFTWARE\\Microsoft\\Windпше ows\\CurrentVersion\\Run /v %s /t REG_SZ /d \"javaw -jar \\\"%s\\\" autostart\" /f",
                    APP_NAME, jarPath);

            // Выполняем команду через процесс
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Успешно добавлено в автозапуск: " + jarPath);

                // Создаём тестовый файл для подтверждения установки
                try {
                    File testFile = new File("D:\\autostart_setup_successful.txt");
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(testFile))) {
                        writer.write("Установка завершена: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                        writer.newLine();
                        writer.write("Путь к JAR: " + jarPath);
                    }
                } catch (IOException e) {
                    System.err.println("Ошибка при создании тестового файла: " + e.getMessage());
                }
                return true;
            } else {
                System.err.println("Не удалось добавить в автозапуск. Код выхода: " + exitCode);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Ошибка при добавлении в автозапуск: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Создает лог-файл с отметкой времени о запуске программы
     */
    private static void createLogFile() {
        try {
            // Формируем текущую дату и время
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());

            // Создаём лог-файл на диске D:
            File logFile = new File("D:\\autostart_log.txt");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
                writer.write("Программа запущена: " + timestamp);
                writer.newLine();
            }
            System.out.println("Лог-файл создан/обновлен: " + logFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Ошибка при создании лог-файла: " + e.getMessage());
        }
    }

    /**
     * Перезагружает компьютер
     */
    private static boolean restartComputer() {
        try {
            System.out.println("Перезагрузка компьютера через 10 секунд...");
            // Запускаем команду для перезагрузки через 10 секунд
            Process process = Runtime.getRuntime().exec("shutdown /r /t 10 /c \"Перезагрузка после добавления в автозапуск\"");
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Команда на перезагрузку успешно выполнена");
                return true;
            } else {
                System.err.println("Ошибка при выполнении команды перезагрузки. Код выхода: " + exitCode);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Ошибка при перезагрузке: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
