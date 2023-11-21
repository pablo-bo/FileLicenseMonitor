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

    public void info() throws IOException {
        System.out.println("  wait, extract information...");
        RingUtil ring = RingUtil.getInstance();
        String info = ring.getLicenseInfo(this);
        System.out.println(info);
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
