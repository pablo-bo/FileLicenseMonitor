package Monitor.FileLicense;

class ProcessInfo{
    private final String name;
    private final String pid;
    String cmd;
    String user;

    public ProcessInfo(String name, String pid, String cmd, String user) {
        this.name = name;
        this.pid = pid;
        this.cmd = cmd;
        this.user = user;
    }

    @Override
    public String toString() {
        return "ProcessInfo{" +
                "name='" + name + '\'' +
                ", pid='" + pid + '\'' +
                ", cmd='" + cmd + '\'' +
                ", user='" + user + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public String getPid() {
        return pid;
    }
    public String getCmd() {
        return cmd;
    }
    public String getUser() {
        return user;
    }
}