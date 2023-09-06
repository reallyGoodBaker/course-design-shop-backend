package top.rgb39.shop.services;

import top.rgb39.shop.providers.DatabaseProvider;
import top.rgb39.shop.tools.injectable.annotation.Identifier;
import top.rgb39.shop.tools.injectable.annotation.Inject;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CustomerService {

    DatabaseProvider db;

    @Identifier(id = "CustomerService")
    CustomerService(
        @Inject("DatabaseProvider") DatabaseProvider db
    ) {
        this.db = db;
    }

    public void add(String id, String name) throws SQLException {
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("name", name);

        db.add("customer", data);
    }

    public boolean exists(String id) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String[] select = { "name" };
        var result = db.query("customer", String.format("where id='%s'", id), select);
        return result.values().size() > 0;
    }

}
