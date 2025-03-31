package vip.lialun.thread;

import com.google.common.base.Preconditions;

import java.util.concurrent.ThreadFactory;

/**
 * 简单线程工厂
 *
 * @author lialun
 */
public class SimpleThreadFactory implements ThreadFactory {
    private int counter = 0;
    private String prefix;

    public SimpleThreadFactory(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(Preconditions.checkNotNull(r), prefix + "-" + counter++);
    }
}
