package util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ThreadPoolUtil {

    private static final ScheduledExecutorService SERVICE = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    public static ScheduledFuture<?> execute(Runnable runnable, long delay) {
        return SERVICE.schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }
}
