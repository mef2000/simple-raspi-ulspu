package pipes.core;

import abs.parts.Bus;

public class Holder {
    private final Runnable todo = new Runnable() {
        @Override public void run() {
            Fluid out = current.work(tin);
            if(out!=null) open(out.CHAIN);
        }
    };
    private Pipe current = null;
    private Thread thread;
    private Fluid tin;
    public void open(Fluid in) {
        if(in == null) { tin = null; thread = null; return; }
        if(in.PIPE == null) { tin = null; thread = null; return; }

        if(current != null) if(current.PID == in.PIPE.PID) current.stop();
        current = in.PIPE;
        tin = in;
        if(current.THREAD_POLICY == Bus.ANY_BACK_THREAD) { thread = new Thread(todo); thread.setDaemon(true); thread.start(); }
        else if(current.THREAD_POLICY == Bus.RUN_UI_THREAD) Bus.event("RUN_IN_UI", todo);
    }
    public void stop() { tin = null; if(current!=null) current.stop(); thread = null; }
}
