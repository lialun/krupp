package vip.lialun.mybatis;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.stereotype.Component;

import java.sql.Statement;

/**
 * StatementHandler是封装JDBC的Statement操作
 * query方法 执行查询语句
 * update方法 调用execute方法执行插入、更新、删除语句
 * batch方法 将SQL命令添加到批处量执行列表中
 */
@Intercepts(value = {
        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class})
})
@Slf4j
@Component
public class SlowSqlInterceptor implements Interceptor {

    @SuppressWarnings("FieldCanBeLocal")
    private final int SLOW_QUERY_THRESHOLD = 20;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long beginTime = System.currentTimeMillis();
        try {
            return invocation.proceed();
        } finally {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - beginTime;
            if (executionTime > SLOW_QUERY_THRESHOLD) {
                StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
                log.warn("Slow Query: {}，Execution Time: {}ms", statementHandler.getBoundSql().getSql(), executionTime);
            }
        }
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Interceptor.super.plugin(target);
        }
        return target;
    }
}