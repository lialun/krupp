package vip.lialun.collection;

import vip.lialun.BaseTest;
import com.google.common.base.Stopwatch;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class SimpleDelayQueueTest extends BaseTest {
    private SimpleDelayQueue<String> delayQueue;

    @BeforeEach
    public void before() {
        delayQueue = new SimpleDelayQueue<>();
    }

    @Test
    public void enqueueAndClear() {
        delayQueue.enqueue("1", 1, TimeUnit.MINUTES);
        assertEquals(1, delayQueue.size());
        delayQueue.clear();
        assertEquals(0, delayQueue.size());
    }

    @Test
    public void dequeue() {
        delayQueue.enqueue("1", 1, TimeUnit.NANOSECONDS);
        assertEquals("1", delayQueue.dequeue());

        Stopwatch stopwatch = Stopwatch.createStarted();
        delayQueue.enqueue("2", 1, TimeUnit.SECONDS);
        assertEquals("2", delayQueue.dequeue());
        MatcherAssert.assertThat(stopwatch.elapsed(TimeUnit.MILLISECONDS), is(greaterThanOrEqualTo(1000L)));
        delayQueue.clear();
    }

    @Test
    public void tryDequeue() throws InterruptedException {
        delayQueue.enqueue("1", 1, TimeUnit.NANOSECONDS);
        assertEquals("1", delayQueue.tryDequeue().orElse(null));

        delayQueue.enqueue("2", 1, TimeUnit.SECONDS);
        assertFalse(delayQueue.tryDequeue().isPresent());
        Thread.sleep(1000);
        assertEquals("2", delayQueue.tryDequeue().orElse(null));

        delayQueue.clear();
    }

    @Test
    public void peek() throws InterruptedException {
        assertFalse(delayQueue.peek().isPresent());

        delayQueue.enqueue("1", 1, TimeUnit.SECONDS);
        assertEquals("1", delayQueue.peek().orElse(null));
        assertEquals("1", delayQueue.peek().orElse(null));

        Thread.sleep(1000);
        assertEquals("1", delayQueue.peek().orElse(null));
        delayQueue.dequeue();
        assertFalse(delayQueue.peek().isPresent());

        delayQueue.clear();
    }

    @Test
    public void size() {
        assertEquals(0, delayQueue.size());

        delayQueue.enqueue("1", 1, TimeUnit.SECONDS);
        assertEquals(1, delayQueue.size());
        delayQueue.clear();
        assertEquals(0, delayQueue.size());
    }
}
