package vip.lialun.mybatis.typehandler;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.zhuliwa.utils.json.JacksonHelper;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.lang.reflect.Field;

@MappedTypes({Object.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public class MyJacksonTypeHandler extends JacksonTypeHandler {

    public MyJacksonTypeHandler(Class<?> type) {
        super(type);
        setObjectMapper(JacksonHelper.getDefaultMapper());
    }

    public MyJacksonTypeHandler(Class<?> type, Field field) {
        super(type, field);
        setObjectMapper(JacksonHelper.getDefaultMapper());
    }
}