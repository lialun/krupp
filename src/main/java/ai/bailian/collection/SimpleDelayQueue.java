package ai.bailian.collection;

import java.util.Optional;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 延时队列
 * <p>
 * 对java.util.concurrent.DelayQueue的封装。
 * DelayQueue需要放入的对象继承自Delayd，并实现部分方法，这会导致使用起来便利性降低。
 * 使用SimpleDelayQueue不需要任何额外操作，可以直接把任何对象放入队列，并指定超时时间。
 *
 * @author lialun
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class SimpleDelayQueue<E> {
    private DelayQueue<SimpleDelayBean<E>> delayQueue = new DelayQueue<>();

    /**
     * 插入一个元素进入延时队列，并指定过期延时时间
     */
    public void enqueue(E obj, long delayTime, TimeUnit timeUnit) {
        delayQueue.offer(new SimpleDelayBean<>(obj, delayTime, timeUnit));
    }

    /**
     * 取出队列中第一个过期的元素。
     * 如果列队中没有元素、或者队列中元素都没有过期，则会阻塞等待，直到有元素到期。
     *
     * @return 到达延时的元素
     */
    public E dequeue() {
        try {
            return delayQueue.take().getObj();
        } catch (InterruptedException e) {
            //由于方法不可取消,所以不用出现这个异常
            return null;
        }
    }

    /**
     * 尝试取出队列中第一个过期的元素。
     * 如果列队中没有元素、或者队列中元素都没有过期，则会返回{@code Optional.empty()}。
     *
     * @return 到达延时的元素
     */
    public Optional<E> tryDequeue() {
        SimpleDelayBean<E> bean = delayQueue.poll();
        return bean == null ? Optional.empty() : Optional.of(bean.getObj());
    }

    /**
     * 查看（不取出）队列中过期时间最早的数据，如果队列没有数据则返回{@code Optional.empty()}。
     * 不同于{@code #dequeue()}，就算数据没有过期也可以被查看到。
     *
     * @return 队列中延时最早到期的数据，如果队列没有数据则返回{@code Optional.empty()}。
     */
    public Optional<E> peek() {
        SimpleDelayBean<E> bean = delayQueue.peek();
        return bean == null ? Optional.empty() : Optional.of(bean.getObj());
    }

    /**
     * 清空队列
     */
    public void clear() {
        delayQueue.clear();
    }

    /**
     * 查看队列大小
     *
     * @return 队列大小
     */
    public int size() {
        return delayQueue.size();
    }
}

@SuppressWarnings("WeakerAccess")
class SimpleDelayBean<E> implements Delayed {
    private long time;
    private E obj;

    SimpleDelayBean(E obj, long delayTime, TimeUnit timeUnit) {
        this.obj = obj;
        this.time = System.nanoTime() + TimeUnit.NANOSECONDS.convert(delayTime, timeUnit);
    }

    public E getObj() {
        return obj;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(time - System.nanoTime(), TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        long compare = this.getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS);
        return (compare == 0) ? 0 : ((compare < 0) ? -1 : 1);
    }
}
