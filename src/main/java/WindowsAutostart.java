import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WindowsAutostart {
    private static final String APP_NAME = "MyJavaAutostartApp";

    public static void main(String[] args) {
        // Check if this is the first run or started from autostart
        boolean isFirstRun = args.length == 0;

        if (isFirstRun) {
            System.out.println("First run detected. Adding to startup...");
            // Add to startup
            if (addToStartup()) {
                // Restart computer
                restartComputer();
            }
        } else {
            // This is a run from startup, create log file
            createLogFile();

            // Here you can add your main application code
            System.out.println("Application running from startup...");
        }
    }

    /**
     * Add the current application to Windows startup via registry
     */
    private static boolean addToStartup() {
        try {
            // Get the path to the current JAR file
            String jarPath = new File(WindowsAutostart.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI()).getAbsolutePath();

            // Fix any path issues
            jarPath = jarPath.replace("/", "\\");

            // Create registry command with startup argument for detection
            String command = String.format(
                    "reg add HKCU\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run /v %s /t REG_SZ /d \"javaw -jar \\\"%s\\\" autostart\" /f",
                    APP_NAME, jarPath);

            // Execute command
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Successfully added to startup: " + jarPath);

                // Also create a test file to confirm the program ran
                try {
                    File testFile = new File("C:\\autostart_setup_successful.txt");
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(testFile))) {
                        writer.write("Setup completed at: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                        writer.newLine();
                        writer.write("JAR path: " + jarPath);
                    }
                } catch (IOException e) {
                    System.err.println("Error creating test file: " + e.getMessage());
                }

                return true;
            } else {
                System.err.println("Failed to add to startup. Exit code: " + exitCode);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error adding to startup: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Create a log file with timestamp of execution directly on C: drive
     */
    private static void createLogFile() {
        try {
            // Create log directory on C: drive
            Path logDir = Paths.get("C:\\AutostartLogs");
            if (!Files.exists(logDir)) {
                Files.createDirectories(logDir);
            }

            // Create log file with current timestamp
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());
            File logFile = new File(logDir.toFile(), "autostart_log.txt");

            // Also create a file directly at C: root for testing
            File rootLogFile = new File("C:\\autostart_log.txt");

            // Append to log files
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
                writer.write("Application executed at: " + timestamp);
                writer.newLine();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(rootLogFile, true))) {
                writer.write("Application executed at: " + timestamp);
                writer.newLine();
            }

            System.out.println("Log files created/updated at: " + logFile.getAbsolutePath() + " and " + rootLogFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error creating log file: " + e.getMessage());
            e.printStackTrace();

            // Try creating file with lower permissions requirement
            try {
                File fallbackFile = new File(System.getProperty("java.io.tmpdir"), "autostart_fallback_log.txt");
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(fallbackFile, true))) {
                    writer.write("Application executed at: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    writer.newLine();
                    writer.write("Error writing to C: drive: " + e.getMessage());
                }
                System.out.println("Fallback log file created at: " + fallbackFile.getAbsolutePath());
            } catch (IOException fallbackError) {
                System.err.println("Error creating fallback log file: " + fallbackError.getMessage());
            }
        }
    }

    /**
     * Restart the computer
     */
    private static boolean restartComputer() {
        try {
            System.out.println("Restarting computer in 10 seconds...");
            // Use shutdown command with restart flag and 10 second delay
            Process process = Runtime.getRuntime().exec("shutdown /r /t 10 /c \"Restarting computer after adding application to startup\"");
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Restart command executed successfully");
                return true;
            } else {
                System.err.println("Failed to restart computer. Exit code: " + exitCode);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error restarting computer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}