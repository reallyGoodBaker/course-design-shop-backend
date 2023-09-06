package top.rgb39.shop.providers;

import top.rgb39.shop.tools.entity.BasicEntity;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public interface QueryResult<T extends BasicEntity> {
    boolean done();

    List<T> values() throws InvocationTargetException, InstantiationException, IllegalAccessException, SQLException;
}