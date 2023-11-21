package Monitor.FileLicense;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

public class RingUtil {
    private static RingUtil INSTANCE;
    private String path;
    private static final String utilName = "ring.cmd";
    public  String version;
    private boolean isAvailable;

    private RingUtil() {
        try {
            path = pathToUtil();
        } catch (URISyntaxException e) {
            // Исключение не выбрасываем, утилита не доступна и все
            isAvailable = false;
        }
        try {
            isAvailable = checkAvailable(path);
        } catch (IOException e) {
            isAvailable = false;
        }
    }
    public static RingUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RingUtil();
        }
        return INSTANCE;
    }

    private String pathToUtil() throws URISyntaxException {
        // 1 - Считаем что утилита прописана в системном PATH
        this.path = utilName;
        return utilName;

    }
    private boolean checkAvailable(String pathToUtil) throws IOException {
        Process p = Runtime.getRuntime().exec(pathToUtil+" --version");
        try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = input.readLine()) != null) {
                // При запросе версии в выводе будет просто номер версии вида 0.19.5-12 и слова ring тут нет,
                // а если ring не установлен то будет сообщение типа "ring" не является внутренней или внешней
                //командой, исполняемой программой или пакетным файлом. - и слово ring тут повторяется
                if (!line.contains("ring")) {
                    version = line;
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Неожиданная ошибка ввода/вывода.");
            return false;
        }
        System.out.println("Утилита ring.cmd не найдена.");
        return false;
    }

    //Возвращает имя лицензии по переданному файлу лицензии
    //Пояснение Имя файла лицензии не является именем лицензии
    //Имя лицензии зашифровано в содержимом файла
    public String getLicenseName(License license) throws IOException {
        Process p = Runtime.getRuntime().exec(path + " license list --send-statistics false");
        try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = input.readLine()) != null) {
                if (line.contains(license.getLicFileName())) {
                    //строка имеет вид 010100000000002-800000001 (имя файла: "20200626133114.lic")
                    //левая часть до пробела - наше имя лицензии
                    String licName = line.split(" ")[0];
                    return licName;
                }
            }
        } catch (IOException e) {
            System.out.println("Неожиданная ошибка ввода/вывода.");
            return "";
        }

        return "";
    }

    //Проверяет валидность файла лицензии.
    public String validate(License license) throws IOException {
        if (! this.isAvailable) {
            return  "Util ring not available."; // TODO выбросить исключение
        }
        String licName = getLicenseName(license);
        Process p = Runtime.getRuntime().exec(path + " license validate --name "+licName+" --send-statistics false");
        try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream(),"Cp1251"))) {
            String line;
            while ((line = input.readLine()) != null) {
                if (line.contains(licName)) {
                    //строка имеет вид Проверка лицензии "010100000000002-800000001" (файл: "010100000000002-800000001") выполнена успешно.
                    String validate = line;//line.split(")")[1];
                    return validate;
                }
            }
        } catch (IOException e) {
            System.out.println("Неожиданная ошибка ввода/вывода.");
            return "";
        }
        return "";
    }

}
