package pipes.core;

public abstract class Pipe {
    public final int THREAD_POLICY;
    public final  String PID;
    public Pipe(String PID, int thread_policy) {
        THREAD_POLICY = thread_policy;
        this.PID = PID;
    }
    public abstract void next(Pipe next);
    public abstract Pipe manager(String event);
    public abstract Fluid work(Fluid in);
    public abstract  void connect(Pipe connect);
    public abstract void stop(boolean isStop);
}
