package Monitor.FileLicense;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

// Да это синглтон. Я понимаю его недостатки, но в данном случае считаю его применение уместным.
// Вначале это был обычный класс, но для проверки каждой лицензии приходилось-бы создавать экземпляр класса
// и выполнять чтение конфига и проверку наличия утилиты handle.exe
// Потом я сделал все методы статическими, но при прверке все равно нужно читать конфиг для получения пути к утилите и проверять ее наличие
// А в синглтоне чтение конфига и проверку нужно делать только один раз - при первом обращении.
// Недостаток - Зависимость обычного класса или метода от синглтона не видна в публичном контракте класса.

// Это синглтон с ленивой загрузкой, но НЕ ПОТОКОБЕЗОПАСНЫЙ!! Многопоточное применение не предполагается!
public final class HandleUtil {
    private static HandleUtil INSTANCE;
    private String path;
    private boolean isAvailable;

    private HandleUtil() {
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

    public static HandleUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HandleUtil();
        }
        return INSTANCE;
    }

    private static String pathToUtil() throws URISyntaxException {
        String pathToHandle;
        Properties properties = new Properties();
        String separator = System.getProperty("file.separator");
        // 1 - ищем конфиг рядом с программой
        URL url = MonitorFileLicense.class.getProtectionDomain().getCodeSource().getLocation();
        File jarFile = new File(url.toURI());
        File jarDir = jarFile.getParentFile(); //
        String pathToConfig = jarDir + separator + "handle.config";
        // 2 - по умолчанию предполагаем стандартный путь в каталоге Utils рядом с программой
        pathToHandle = jarDir + separator + "Utils" + separator + "handle.exe";
        // 3 - если конфиг есть, считеем его
        try (FileInputStream fis = new FileInputStream(pathToConfig)) {
            properties.load(fis);
            pathToHandle = properties.getProperty("pathhandle");
            //System.out.println(path);
        } catch (FileNotFoundException ex) {
            System.out.println("Не найден файл конфигурации handle.config.");
            System.out.println("Ожидааемое расположение файла конфигурации: " + pathToConfig);
            System.out.println("Предполагаем что путь к утилите: " + pathToHandle);
        } catch (IOException ex) {
            System.out.println("Неожиданная ошибка ввода/вывода.");
        }
        return pathToHandle;
    }

    private boolean checkAvailable(String pathToUtil) throws IOException {
        Process p = Runtime.getRuntime().exec(pathToUtil+" -help");
        try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = input.readLine()) != null) {
                if (line.contains("Handle viewer")) {
                   return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Неожиданная ошибка ввода/вывода.");
            return false;
        }
        System.out.println("Утилита handle.exe не найдена.");
        return false;
    }

    public List<ProcessInfo> used(License license) throws IOException {
        List<ProcessInfo> result = new ArrayList<>();
        if (! this.isAvailable) {
           return  result; // TODO выбросить исключение
        }
        String execPath = this.path + " " + license.getLicFileName();
        Process p = Runtime.getRuntime().exec(execPath);
        try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = input.readLine()) != null) {
                if (line.contains("pid:")) {
                    // parsing name and pid
                    String processName = line.split(" ")[0];
                    String processPid = line.split("pid: ")[1].split(" ")[0];
                    ProcessHandle pHandle = ProcessHandle.of(Long.parseLong(processPid)).orElse(null);
                    ProcessInfo pInfo = new ProcessInfo(processName, processPid, pHandle.info().commandLine().orElse(""), pHandle.info().user().orElse(""));
                    result.add(pInfo);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
