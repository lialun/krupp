package vip.lialun.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * 多线程执行容器,可以设置线程数量
 *
 * @author lialun
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class MultiThreadExecutor {
    private ExecutorService executorService;
    List<FutureTask> futures = new ArrayList<>();
    /**
     * 容器当前状态
     */
    private int executorStatus = EXECUTOR_STATUS_INIT;

    /**
     * 容器状态：初始化(运行中)
     */
    private static final int EXECUTOR_STATUS_INIT = 0;
    /**
     * 容器状态：关闭(停止接收新任务)
     */
    private static final int EXECUTOR_STATUS_SHUTDOWN = 1;
    /**
     * 容器状态：已终止
     */
    private static final int EXECUTOR_STATUS_FINISH = 2;

    public MultiThreadExecutor(int threads) {
        this(threads, "ThreadExecutor");
    }

    public MultiThreadExecutor(int threads, String threadPrefix) {//, new DaemonThreadFactory("MultiThreadExecutor")
        this.executorService = Executors.newFixedThreadPool(threads, new SimpleThreadFactory(threadPrefix));
    }

    /**
     * 添加任务。
     * 如果可能用到#{@link #terminate()},不要捕捉#{@link InterruptedException}
     */
    public synchronized void addTask(Callable callable) {
        if (executorStatus != EXECUTOR_STATUS_INIT) {
            throw new ExecutorException("需要在执行getResult、terminate方法前添加任务");
        }
        futures.add((FutureTask) executorService.submit(callable));
    }

    /**
     * 获取任务执行结果。任务执行后容器关闭。
     */
    public synchronized List<ExecuteResult> getResult(long timeout, TimeUnit timeUnit) {
        if (executorStatus == EXECUTOR_STATUS_SHUTDOWN) {
            throw new ExecutorException("getResult方法只能调用一次");
        }
        if (executorStatus == EXECUTOR_STATUS_FINISH) {
            throw new ExecutorException("容器已经关闭");
        }
        List<ExecuteResult> results = new ArrayList<>();
        executorService.shutdown();
        executorStatus = EXECUTOR_STATUS_SHUTDOWN;
        waitFinish(timeout, timeUnit);
        terminate();
        for (FutureTask futureTask : futures) {
            if (!futureTask.isDone()) {
                futureTask.cancel(true);
            }
            ExecuteResult executeResult = new ExecuteResult();
            try {
                executeResult.setResult(Optional.of(futureTask.get(0, timeUnit)));
                executeResult.setExecuteSuccess(true);
            } catch (ExecutionException e) {
                executeResult.setException(e.getCause());
                executeResult.setExecuteSuccess(false);
            } catch (Exception e) {
                executeResult.setException(e);
                executeResult.setExecuteSuccess(false);
            }
            results.add(executeResult);
        }
        return results;
    }

    public void waitFinish(long timeout, TimeUnit timeUnit) {
        try {
            executorService.shutdown();
            executorService.awaitTermination(timeout, timeUnit);
        } catch (InterruptedException e) {
            //线程如果被中断,基本可以确定是连接池被关闭。这时继续执行后续代码获取返回值即可。
        }
    }

    public void waitFinish() {
        waitFinish(Long.MAX_VALUE, TimeUnit.DAYS);
    }

    /**
     * 获取任务执行结果。任务执行后容器关闭。
     */
    public List<ExecuteResult> getResult() {
        return getResult(Long.MAX_VALUE, TimeUnit.DAYS);
    }

    /**
     * 任务执行结束后关闭容器
     */
    public void shutdownAfterAllTaskFinish() {
        executorService.shutdown();
    }

    /**
     * 停止所有任务,关闭容器
     */
    public synchronized void terminate() {
        if (executorStatus == EXECUTOR_STATUS_FINISH) {
            return;
        }
        executorService.shutdownNow();
        executorStatus = EXECUTOR_STATUS_FINISH;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public class ExecuteResult {
        boolean isExecuteSuccess;
        Optional result;
        Throwable exception;

        public boolean isExecuteSuccess() {
            return isExecuteSuccess;
        }

        private void setExecuteSuccess(boolean executeSuccess) {
            isExecuteSuccess = executeSuccess;
        }

        public Optional getResult() {
            return result;
        }

        private void setResult(Optional result) {
            this.result = result;
        }

        public Throwable getException() {
            return exception;
        }

        private void setException(Throwable exception) {
            this.exception = exception;
        }
    }

    public class ExecutorException extends RuntimeException {
        ExecutorException(String message) {
            super(message);
        }
    }
}
