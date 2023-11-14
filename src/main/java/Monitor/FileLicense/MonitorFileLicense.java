package Monitor.FileLicense;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MonitorFileLicense {
    private static final String standardLicenseDirPath = "C:\\ProgramData\\1C\\licenses";

    private static List<License> fillLicensesList(String licenseDirPath) {
        List<License> licenseList = new ArrayList<License>();
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
            System.out.println(String.format("[%s] License file name: %s, file path: %s",enumerator, lic.getLicFileName(), lic.getLicFilePath()));
            enumerator++;
            if(validate) { lic.validate(); }
            if(use)      { lic.used();}
        }
    }
    public static void main(String[] args) throws IOException {
        System.out.println("File license monitor for 1C v0.1");
        //System.out.println("args = "+Arrays.asList(args));
        // TODO parsing args  -v:validate, -u:usage -p:path_to_licenses_dir
        List<String> arguments = Arrays.asList(args);
        boolean used = arguments.contains("-u");
        boolean validate = arguments.contains("-v");

        List<License> licList = fillLicensesList(standardLicenseDirPath);

        if (used){
            // во время первого вызова будет произведена проверка доступности
            HandleUtil.getInstance();
        }
        printLicensesList(licList,validate, used);
        System.out.println("Done.");
    }
}