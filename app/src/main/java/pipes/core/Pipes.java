package pipes.core;

import abs.parts.Bus;

public class Pipes {
    private final Runnable worker = new Runnable() {
        @Override public void run() {
            open(current.work(work));
        }
    };
    private Pipe current;
    private Fluid work;
    public void open(Fluid start) {
        if(start==null) { work = null; current = null; return; }

        work = start;
        current = start.PIPE;
        if(current == null) { work = null; return; }

        if(current.THREAD_POLICY == Bus.ANY_BACK_THREAD) { Thread thread = new Thread(worker); thread.setDaemon(true); thread.start(); }
        else if(current.THREAD_POLICY == Bus.RUN_UI_THREAD) {
            Bus.event("RUN_IN_UI", worker);
        }
    }

    public Runnable getWorker() { return worker; }
}
