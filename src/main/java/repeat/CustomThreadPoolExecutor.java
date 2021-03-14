package main.java.repeat;

import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CustomThreadPoolExecutor extends ThreadPoolExecutor {
    public CustomThreadPoolExecutor(int countThreads) {
        super(countThreads, countThreads, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }

    @Override
    public void execute(Runnable command) {
        int repeats = getRepeatAnnotation(command);
        for (int i = 0; i < repeats; i++) {
            super.execute(command);
        }
    }

    private int getRepeatAnnotation(Runnable command) {
        Repeat repeat = command.getClass().getAnnotation(Repeat.class);
        return Objects.nonNull(repeat) ? repeat.value() : 1;
    }
}
