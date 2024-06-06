package common.concurency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public enum Executor {
    EXECUTOR;
    private ExecutorService executorService;

    public void injectExecutor(ExecutorService service) {
        if (executorService == null) {
            executorService = service;
        }
    }

    public void defaultImplementation(int numberOfThreads) {
        if (executorService == null) {
            executorService = Executors.newFixedThreadPool(numberOfThreads);
        }
    }

    public void execute(Runnable runnable) {
        executorService.execute(runnable);
    }

}
