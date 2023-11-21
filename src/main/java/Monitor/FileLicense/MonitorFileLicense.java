package Monitor.FileLicense;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MonitorFileLicense {
    private static final String standardLicenseDirPath = "C:\\ProgramData\\1C\\licenses";

    private static List<License> fillLicensesList(String licenseDirPath) {
        List<License> licenseList = new ArrayList<>();
        File licenseDir = new File(licenseDirPath);
        FilenameFilter licFileFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                String lowercaseName = name.toLowerCase();
                return lowercaseName.endsWith(".lic");
            }
        };

        File[] fileList = licenseDir.listFiles(licFileFilter);
        for (File file : fileList) {
            licenseList.add(new License(file));
        }
        return licenseList;
    }
    private static void printLicensesList(List<License> licensesList, boolean validate, boolean use) throws IOException {
        int enumerator = 0;
        for (License lic : licensesList) {
            System.out.printf("[%s] License file name: %s, file path: %s%n",enumerator, lic.getLicFileName(), lic.getLicFilePath());
            enumerator++;
            if(validate) { lic.validate(); }
            if(use)      { lic.used();}
        }
    }
    public static void main(String[] args) throws IOException {
        String LicenseDirPath;
        System.out.println("File license monitor for 1C v0.1");
        // parsing args  -v:validate, -u:usage -p:path_to_licenses_dir
        List<String> arguments = Arrays.asList(args);
        boolean used     = arguments.contains("-u");
        boolean validate = arguments.contains("-v");
        boolean usePath  = arguments.contains("-p");
        if (usePath){
            int idxPath = arguments.indexOf("-p")+1;
            if (arguments.size()>idxPath){
                //В аргументах есть путь
                LicenseDirPath = arguments.get(idxPath);
            }else {
                System.out.println("Забыли указать путь в аргументах, поиск будет по стандартному пути");
                LicenseDirPath = standardLicenseDirPath;
            }
        }else {
            LicenseDirPath = standardLicenseDirPath;
        }

        System.out.println("Каталог лицензий: "+LicenseDirPath);
        //Нужно проверить существование этого пути
        File validateDir = new File(LicenseDirPath);
        if (!validateDir.isDirectory()) {
            throw new IOException("Указанного каталога не существает!");
        }

        List<License> licList = fillLicensesList(LicenseDirPath);

        if (used){
            HandleUtil.getInstance();// во время первого вызова будет произведена проверка доступности
        }
        if (validate){
            RingUtil.getInstance();// во время первого вызова будет произведена проверка доступности
            System.out.println("available ring version is :"+RingUtil.getInstance().version);
        }
        printLicensesList(licList,validate, used);
        System.out.println("Done.");
    }
}