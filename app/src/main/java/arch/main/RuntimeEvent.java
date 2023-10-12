package arch.main;

public class RuntimeEvent {
    public Object transfer;
    protected volatile boolean isKilled = false;
    public void asyncWork() throws Exception {}
    public void postWorkMainUI() {}
    public void preWorkMainUI() {
        isKilled = false;
    }
    public void kill() {
        this.isKilled = true;
    }
}
