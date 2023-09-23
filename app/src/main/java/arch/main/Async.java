package arch.main;

import android.os.Handler;
import android.os.Message;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Async {
    private static ConcurrentHashMap<String, Thread> pool = new ConcurrentHashMap<>();
    private static Handler handler;
    private static RuntimeEvent error_cb;
    public static void init(Handler manager, RuntimeEvent error_callback) { handler = manager; error_cb = error_callback; }
    public static void startAsync(RuntimeEvent rte, String ID_EVENT) {
        if(pool.containsKey(ID_EVENT)) {
            Thread dead = pool.get(ID_EVENT);
            try {
                System.out.println("MUST_BE_KILL_ASYNC: "+ID_EVENT);
                dead.interrupt();
                dead.stop();
            }catch(Exception e) { e.printStackTrace();}
        }
        if(rte!=null) {
            rte.preWorkMainUI();
            Thread live = new Thread(new Runnable() {
                Message msg = new Message();
                @Override
                public void run() {
                    try {
                        rte.asyncWork();
                        msg.obj = rte;
                    }catch(Exception e) {
                        //e.printStackTrace();
                        msg.obj = error_cb;
                        StringWriter sw = new StringWriter();
                        e.printStackTrace(new PrintWriter(sw));
                        Data.error_stack = sw.toString();
                    }
                    handler.sendMessage(msg);
                    //pool.remove();
                }
            });
            pool.put(ID_EVENT, live);
            live.start();
        }else System.out.println("ASYNC_WRONG_RT_EVENT: null --> skipping.");
    }
}
