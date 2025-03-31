package vip.lialun.mybatis;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Mybatis Plus 分页
 *
 * @author lialun
 */
@SuppressWarnings("unused")
public class Page<T> implements IPage<T> {
    @Serial
    private static final long serialVersionUID = 3790179253012755214L;

    /**
     * 自动优化 COUNT SQL
     */
    private static final boolean OPTIMIZE_COUNT_SQL = true;

    /**
     * 数据列表
     */
    private List<T> records = Collections.emptyList();

    /**
     * 总数
     */
    private long total = 0;

    /**
     * 每页显示条数，默认 10
     */
    private long size = 10;

    /**
     * 当前页
     */
    private long current = 1;

    /**
     * 排序字段信息
     */
    @Getter
    private final List<OrderItem> orders = new ArrayList<>();

    /**
     * 是否进行 count 查询
     */
    private boolean isSearchCount = true;

    /**
     * 最大每页内容条数限制
     * -- SETTER --
     * 设置最大每页内容条数限制
     */
    @Setter
    protected Long maxLimit = 0L;

    public Page() {
    }

    public Page(long current, long size) {
        this(current, size, true, 0L);
    }

    /**
     * 创建分页
     *
     * @param current       当前页数
     * @param size          每页显示条数
     * @param isSearchCount 是否进行 count 查询
     * @param maxLimit      最大每页内容条数限制
     */
    public Page(long current, long size, boolean isSearchCount, long maxLimit) {
        if (current > 1) {
            this.current = current;
        }
        this.size = size;
        this.isSearchCount = isSearchCount;
        this.maxLimit = maxLimit;
    }

    /**
     * 是否存在上一页
     *
     * @return true / false
     */
    @JsonGetter
    public boolean isHasPrevious() {
        return this.current > 1;
    }

    /**
     * 是否存在下一页
     *
     * @return true / false
     */
    public boolean isHasNext() {
        return this.current < this.getPages();
    }

    /**
     * 添加新的排序条件
     */
    public Page<T> addOrder(OrderItem... items) {
        orders.addAll(Arrays.asList(items));
        return this;
    }

    /**
     * 添加多个新的排序条件
     */
    public Page<T> addOrder(List<OrderItem> items) {
        orders.addAll(items);
        return this;
    }

    @Override
    public List<T> getRecords() {
        return this.records;
    }

    @Override
    public Page<T> setRecords(List<T> records) {
        this.records = records;
        return this;
    }

    @Override
    public long getTotal() {
        return this.total;
    }

    @Override
    public Page<T> setTotal(long total) {
        this.total = total;
        return this;
    }

    @Override
    public long getSize() {
        return this.size;
    }

    @Override
    public Page<T> setSize(long size) {
        this.size = size;
        return this;
    }

    @Override
    public long getCurrent() {
        return this.current;
    }

    @Override
    public Page<T> setCurrent(long current) {
        this.current = current;
        return this;
    }

    @Override
    public Long maxLimit() {
        return maxLimit;
    }

    @Override
    public List<OrderItem> orders() {
        return orders;
    }

    @Override
    @JsonIgnore
    public boolean optimizeCountSql() {
        return OPTIMIZE_COUNT_SQL;
    }

    @Override
    @JsonIgnore
    public boolean searchCount() {
        return isSearchCount;
    }
}
