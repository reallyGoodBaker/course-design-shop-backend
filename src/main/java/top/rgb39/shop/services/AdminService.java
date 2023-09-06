package top.rgb39.shop.services;

import top.rgb39.shop.entities.AdminEntity;
import top.rgb39.shop.providers.DatabaseProvider;
import top.rgb39.shop.providers.QueryResult;
import top.rgb39.shop.tools.injectable.annotation.Identifier;
import top.rgb39.shop.tools.injectable.annotation.Inject;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminService {

    DatabaseProvider db;

    @Identifier(id = "AdminService")
    AdminService(
        @Inject("DatabaseProvider") DatabaseProvider db
    ) {
        this.db = db;
    }

    public void add(String id, String passwd) throws SQLException {
        Map<String, Object> data = new HashMap<>();
        data.put("name", id);
        data.put("passwd", passwd);
        db.add("admin", data);
    }

    public boolean valid(String id, String passwd) throws SQLException {
        String[] select = { "passwd" };
        QueryResult<AdminEntity> admin = db.query("admin", String.format("where name='%s'", id), select);

        if (!admin.done()) return false;

        try {
            return admin.values().get(0).passwd.equals(passwd);
        } catch (Exception e) {
            return false;
        }
    }

    public void delete(String id) throws SQLException {
        db.delete("admin", String.format("name='%s'", id));
    }

    public void update(String id, String passwd) throws SQLException {
        Map<String, Object> data = new HashMap<>();

        data.put("id", id);
        data.put("passwd", passwd);

        db.update("admin", String.format("name='%s'", id), data);
    }

    public List<String> names() throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String[] select = { "name" };
        List<String> names = new ArrayList<>();
        QueryResult<AdminEntity> result = db.query("admin", "", select);
        for (AdminEntity entity: result.values()) {
            names.add(entity.name);
        }

        return names;
    }
}
