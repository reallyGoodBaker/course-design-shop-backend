package top.rgb39.shop.services;

import top.rgb39.shop.entities.GoodsEntity;
import top.rgb39.shop.providers.DatabaseProvider;
import top.rgb39.shop.providers.QueryResult;
import top.rgb39.shop.tools.injectable.annotation.Identifier;
import top.rgb39.shop.tools.injectable.annotation.Inject;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoodsService {

    DatabaseProvider db;

    @Identifier(id = "GoodsService")
    GoodsService(
        @Inject("DatabaseProvider") DatabaseProvider db
    ) {
        this.db = db;
    }

    public void add(
        String id,
        String name,
        float price,
        int stock
    ) throws SQLException {
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("name", name);
        data.put("price", price);
        data.put("stock", stock);
        db.add("goods", data);
    }

    public List<?> query(
        Map<String, Object> options,
        List<String> fields
    ) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return db.query(
                "goods",
                options,
                fields.toArray(new String[0])
        ).values();
    }

    public void delete(
        String id
    ) throws SQLException {
        db.delete("goods", String.format("id='%s'", id));
    }

    public void update(
        String id,
        String name,
        float price,
        int stock
    ) throws SQLException {
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> condition = new HashMap<>();

        condition.put("id", id);

        data.put("id", id);
        data.put("name", name);
        data.put("price", price);
        data.put("stock", stock);
        db.update("goods", condition, data);
    }

    public GoodsEntity find(String id) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String[] select = { "id", "name", "price", "stock" };
        QueryResult<GoodsEntity> result = db.query("goods", String.format("where id='%s'", id), select);
        return result.values().get(0);
    }

    public List<?> matchedGoods(String pattern) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String[] select = { "id", "name", "price", "stock" };
        return db.query("goods", String.format("where name like '%s'", pattern), select).values();
    }
}
