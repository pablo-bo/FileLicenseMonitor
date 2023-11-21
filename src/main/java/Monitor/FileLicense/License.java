package Monitor.FileLicense;

import java.io.*;
import java.util.List;

public class License {
    private final File licFile;
    public License(File licFile) {
        this.licFile = licFile;
    }
    public String getLicFileName() {
        return licFile.getName();
    }
    public String getLicFilePath() {
        return licFile.getAbsolutePath();
    }

    public void printInfo() {
        try (BufferedReader reader = new BufferedReader(new FileReader(licFile.getAbsolutePath()))) {
            String line = reader.readLine();
            while (line != null) {
                System.out.println(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public  void validate() throws IOException {
        System.out.print("  wait, validating - ");
        RingUtil ring = RingUtil.getInstance();
        String resValidate = ring.validate(this);
        System.out.println("  result: "+resValidate);

    }
    public  void used() throws IOException {
        System.out.print("  wait, searching using - ");
        HandleUtil Handle = HandleUtil.getInstance();
        List<ProcessInfo> usedProcesses = Handle.used(this);
        if(usedProcesses.isEmpty()){
            System.out.println("  not used");
        }else{
            System.out.println("  used:");
            for (ProcessInfo pInfo : usedProcesses) {
                System.out.print("      -");
                System.out.println(pInfo);
            }

        }
    }
}
