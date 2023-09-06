package top.rgb39.shop.services;

import top.rgb39.shop.providers.DatabaseProvider;
import top.rgb39.shop.tools.injectable.annotation.Identifier;
import top.rgb39.shop.tools.injectable.annotation.Inject;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DealService {

    DatabaseProvider db;

    @Identifier(id = "DealService")
    DealService(
        @Inject("DatabaseProvider") DatabaseProvider db
    ) {
        this.db = db;
    }

    public void add(
        String did,
        String cid,
        String detail,
        Float cost,
        long date
    ) throws SQLException {
        Map<String, Object> data = new HashMap<>();

        data.put("did", did);
        data.put("cid", cid);
        data.put("detail", detail);
        data.put("cost", cost);
        data.put("date", date);

        db.add("deal", data);
    }

    public void delete(String did) throws SQLException {
        Map<String, Object> condition = new HashMap<>();
        condition.put("did", did);

        db.delete("deal", condition);
    }

    public List<?> query(
        Map<String, Object> conditions,
        List<String> fields,
        Map<String, Object> options
    ) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return db.query(
            "deal",
                conditions,
                fields.toArray(new String[0]),
                options
        ).values();
    }
}
