package top.totoro.plugin.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThreadPoolUtil {

    private static final ScheduledExecutorService SERVICE = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    public static void execute(Runnable runnable, long delay) {
        SERVICE.schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }
}
