package top.rgb39.shop.providers;

import top.rgb39.shop.tools.ArrayUtils;
import top.rgb39.shop.tools.entity.BasicEntity;
import top.rgb39.shop.tools.entity.EntityFactory;
import top.rgb39.shop.tools.entity.annotation.FieldTypes;
import top.rgb39.shop.tools.entity.annotation.Prop;
import top.rgb39.shop.tools.entity.annotation.Result;
import top.rgb39.shop.tools.injectable.annotation.Identifier;
import top.rgb39.shop.tools.injectable.annotation.Inject;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.sql.Date;
import java.sql.*;
import java.util.*;


public class DatabaseProvider {

    Connection conn;
    String name;
    List<Class<?>> entityCls = null;

    static Map<FieldTypes, String> fieldTypesMap = new HashMap<>();

    static {
        fieldTypesMap.put(FieldTypes.ARRAY, "array");
        fieldTypesMap.put(FieldTypes.BINARY_STREAM, "LONGVARBINARY");
        fieldTypesMap.put(FieldTypes.BLOB, "blob");
        fieldTypesMap.put(FieldTypes.BOOLEAN, "BOOLEAN");
        fieldTypesMap.put(FieldTypes.DATE, "date");
        fieldTypesMap.put(FieldTypes.DOUBLE, "double");
        fieldTypesMap.put(FieldTypes.FLOAT, "float");
        fieldTypesMap.put(FieldTypes.INT, "int");
        fieldTypesMap.put(FieldTypes.NULL, "null");
        fieldTypesMap.put(FieldTypes.STRING, "varchar(768)");
        fieldTypesMap.put(FieldTypes.TEXT, "text");
        fieldTypesMap.put(FieldTypes.LONG, "bigint");
    }


    @Identifier(id = "DatabaseProvider", singleton = true)
    DatabaseProvider(
        @Inject("ConfigProvider") ConfigProvider config,
        @Inject("DataSourceProvider") DataSourceProvider ds
    ) throws ClassNotFoundException {
        String rootName = config.root();
        List<Class<?>> entityCls = new ArrayList<>();

        for (String name: config.entities()) {
            String className = String.format("%s.%s", rootName, name);
            entityCls.add(Class.forName(className));
        }

        try {
            this.entityCls = entityCls;
            this.name = config.database().databaseName;
            conn = ds.getConnection();
            initDatabase();
        } catch (Exception e) {
            conn = null;
            e.printStackTrace();
        }

    }

    void initDatabase() throws Exception {
        PreparedStatement ps = conn.prepareStatement("show databases");
        ResultSet rs = ps.executeQuery();

        boolean existed = false;

        while (rs.next()) {
            String databaseName = rs.getString(1);
            if (databaseName.equals(this.name)) {
                existed = true;
                break;
            }
        }

        if (!existed) {
            var _ps = conn.prepareStatement("create database " + this.name);
            _ps.execute();
        }

        conn.prepareStatement("use " + this.name).execute();
        createTables();
    }

    void createTables() throws Exception {
        List<String> tablesExist = showTables();

        for (Class<?> cls: this.entityCls) {
            var en = EntityFactory.getEntity(cls);
            var name = en.getEntityName();
            if (tablesExist.contains(name)) {
                continue;
            }

            StringBuilder sqlStr = new StringBuilder("create table " + name + " (");
            List<String> parts = new ArrayList<>();

            for (var v: en.getEntityFields()) {
                String field = v.field;
                String type = fieldTypesMap.get(v.type);
                String fieldSql = String.format("%s %s", field, type);
                if (v.primaryKey) {
                    fieldSql += " primary key";
                }

                parts.add(fieldSql);
            }

            sqlStr.append(ArrayUtils.join(parts.toArray(new String[0]), ","));
            sqlStr.append(");");

            conn.prepareStatement(sqlStr.toString()).execute();
        }
    }

    public List<String> showTables() throws SQLException {
        List<String> li = new ArrayList<>();
        var rs = conn.prepareStatement("show tables").executeQuery();

        while (rs.next()) {
            li.add(rs.getString(1));
        }

        return li;
    }

    public void add(String table, Map<String, Object> data) throws SQLException {
        BasicEntity entity = EntityFactory.getEntityByName(table);
        if (Objects.isNull(entity)) {
            return;
        }

        List<BasicEntity.FieldValue> fields = entity.getEntityFields();
        List<String> fieldNames = new ArrayList<>();
        List<FieldTypes> types = new ArrayList<>();
        int valueCount = data.size();
        StringBuilder sqlBuilder = new StringBuilder("insert into " + table + "(");

        for (BasicEntity.FieldValue v: fields) {
            String field = v.field;
            if (data.containsKey(field)) {
                fieldNames.add(field);
                types.add(v.type);
            }
        }

        String[] placeholderArr = new String[valueCount];
        Arrays.fill(placeholderArr, "?");
        String placeholder = ArrayUtils.join(placeholderArr, ",");

        sqlBuilder.append(ArrayUtils.join(fieldNames.toArray(new String[0]), ","));
        sqlBuilder.append(") values (");
        sqlBuilder.append(placeholder);
        sqlBuilder.append(")");

        var ps = conn.prepareStatement(sqlBuilder.toString());
        var i = 0;

        for (String name: fieldNames) {
            setParameter(ps, types.get(i), i + 1, data.get(name));
            i++;
        }
        ps.executeUpdate();
    }

    void setParameter(PreparedStatement ps, FieldTypes type, int index, Object data) throws SQLException {
        if (data == null) {
            switch (type) {
                case ARRAY: ps.setNull(index, Types.ARRAY); break;
                case BINARY_STREAM: ps.setNull(index, Types.LONGVARBINARY); break;
                case BLOB: ps.setNull(index, Types.BLOB); break;
                case BOOLEAN: ps.setNull(index, Types.BOOLEAN); break;
                case DATE: ps.setNull(index, Types.DATE); break;
                case DOUBLE: ps.setNull(index, Types.DOUBLE); break;
                case FLOAT: ps.setNull(index, Types.FLOAT); break;
                case LONG: ps.setNull(index, Types.BIGINT); break;
                case INT: ps.setNull(index, Types.INTEGER); break;
                case NULL: ps.setNull(index, Types.NULL); break;
                case STRING:
                case TEXT:
                    ps.setNull(index, Types.VARCHAR); break;
            }

            return;
        }

        switch (type) {
            case ARRAY: ps.setArray(index, (Array) data); break;
            case BINARY_STREAM: ps.setBinaryStream(index, (InputStream) data); break;
            case BLOB: ps.setBlob(index, (Blob) data); break;
            case BOOLEAN: ps.setBoolean(index, (Boolean) data); break;
            case DATE: ps.setDate(index, (Date) data); break;
            case DOUBLE: ps.setDouble(index, (Double) data); break;
            case FLOAT: ps.setFloat(index, (Float) data); break;
            case LONG: ps.setLong(index, (Long) data); break;
            case INT: ps.setInt(index, (Integer) data); break;
            case NULL: ps.setNull(index, Types.NULL); break;
            case STRING:
            case TEXT:
                ps.setString(index, (String) data); break;
        }
    }

    public void delete(String table, String condition) throws SQLException {
        String sqlStr = "delete from " + table;
        if (!Objects.isNull(condition) && condition.length() > 0) {
            sqlStr += " where " + condition;
        }
        conn.prepareStatement(sqlStr)
                .executeUpdate();
    }

    public void delete(String table, Map<String, Object> condition) throws SQLException {
        String _cond = parseCondition(condition);
        delete(table, _cond);
    }

    public void update(String table, String condition, Map<String, Object> data) throws SQLException {
        BasicEntity entity = EntityFactory.getEntityByName(table);

        List<String> setter = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        List<FieldTypes> types = new ArrayList<>();

        for (BasicEntity.FieldValue fv: entity.getEntityFields()) {
            String field = fv.field;
            if (!data.containsKey(field)) continue;

            setter.add(String.format("%s=?", field));
            values.add(data.get(field));
            types.add(fv.type);
        }

        String sqlStr = String.format(
                "update %s set %s where %s",
                table,
                ArrayUtils.join(
                        setter.toArray(new String[0]),
                        ","
                ),
                condition
        );

        var ps = conn.prepareStatement(sqlStr);
        var i = 0;
        for (Object v: values) {
            setParameter(ps, types.get(i), i + 1, v);
            i++;
        }
        ps.executeUpdate();
    }

    public void update(String table, Map<String, Object> condition, Map<String, Object> data) throws SQLException {
        String _cond = parseCondition(condition);
        update(table, _cond, data);
    }

    public <T extends BasicEntity> QueryResult<T> query(String table, String condition, String[] select) throws SQLException {
        BasicEntity entity = EntityFactory.getEntityByName(table);
        String selecting = select.length > 0
            ? ArrayUtils.join(select, ",")
            : "*";

        var ps = conn.prepareStatement(String.format(
            "select %s from %s %s",
            selecting,
            table,
            condition
        ));


        return new DefaultQueryResult<>(ps.executeQuery(), entity, select);
    }

    public <T extends BasicEntity> QueryResult<T> query(String table, Map<String, Object> condition, String[] select) throws SQLException {
        String _condition = "where " + parseCondition(condition);
        return query(table, _condition, select);
    }

    public <T extends BasicEntity> QueryResult<T> query(
        String table,
        Map<String, Object> condition,
        String[] select,
        Map<String, Object> options
    ) throws SQLException {
        String _condition = "where " + parseCondition(condition);
        Object limit = options.get("limit");
        Object offset = options.get("offset");
        if (limit instanceof Integer) {
            _condition += String.format(" limit %s", limit);
        }
        if (offset instanceof Integer) {
            _condition += String.format(" offset %s", offset);
        }
        return query(table, _condition, select);
    }

    String parseCondition(Map<String, Object> conditions) {
        List<String> conditionList = new ArrayList<>();
        for (Map.Entry<String, Object> condition: conditions.entrySet()) {
            final String key = condition.getKey();
            final Object val = condition.getValue();

            if (val instanceof String) {
                String valStr;
                if (((String) val).contains("..")) {
                    conditionList.add(String.format("%s%s", key, parseRange((String) val)));
                } else {
                    conditionList.add(String.format("%s=%s", key, String.format("'%s'", val)));
                }

                continue;
            }

            conditionList.add(String.format("%s=%s", key, val));
        }

        return ArrayUtils.join(conditionList.toArray(new String[0]), ",");
    }

    String parseRange(String str) {
        boolean not = false;
        if (str.startsWith("!")) {
            not = true;
            str = str.substring(1);
        }

        String[] parts = str.split("\\.\\.");
        double biggerThan;
        double lessThan;

        try {
            biggerThan = Double.parseDouble(parts[0]);
        } catch (Exception e) {
            biggerThan = Double.MIN_VALUE;
        }

        try {
            lessThan = Double.parseDouble(parts[1]);
        } catch (Exception e) {
            lessThan = Double.MAX_VALUE;
        }

        String cond = String.format(" between %s and %s", biggerThan, lessThan);

        return not ? " not" + cond : cond;
    }

    static class DefaultQueryResult<T extends BasicEntity> implements QueryResult<T> {

        ResultSet rs;
        BasicEntity entity;
        ArrayList<String> select;

        DefaultQueryResult(ResultSet rs, BasicEntity entity, String[] select) {
            this.rs = rs;
            this.entity = entity;
            this.select = new ArrayList<>(Arrays.asList(select));
        }

        @Override
        public boolean done() {
            return !Objects.isNull(rs);
        }

        @Override
        public List<T> values() throws InvocationTargetException, InstantiationException, IllegalAccessException, SQLException {
            Map<String, BasicEntity.FieldValue> fields = entity.getEntityFieldMapping();
            List<BasicEntity.FieldValue> fieldValues = entity.getEntityFields();
            Class<?> cls = entity.getClass();
            Constructor<?> entityConstructor = cls.getDeclaredConstructors()[0];

            for (Constructor<?> c: cls.getDeclaredConstructors()) {
                Result res = c.getAnnotation(Result.class);
                if (!Objects.isNull(res)) {
                    entityConstructor = c;
                    break;
                }
            }

            entityConstructor.setAccessible(true);

            List<String> params = new ArrayList<>();
            var i = 0;
            for (Parameter p: entityConstructor.getParameters()) {
                String prop = p.getAnnotation(Prop.class).value();
                if (prop.equals("unset")) prop = fieldValues.get(i).field;

                params.add(prop);
                i++;
            }

            List<T> result = new ArrayList<>();
            while(rs.next()) {
                List<Object> args = new ArrayList<>();
                for (String name: params) {
                    BasicEntity.FieldValue v = fields.get(name);
                    int index = select.indexOf(name) + 1;

                    if (index > 0) {
                        args.add(getValue(rs, index, v.type));
                    } else {
                        args.add(null);
                    }
                }

                result.add((T) entityConstructor.newInstance(args.toArray()));
            }

            return result;
        }

        Object getValue(ResultSet rs, int index, FieldTypes type) throws SQLException {
            switch (type) {
                case ARRAY: return rs.getArray(index);
                case BINARY_STREAM: return rs.getBinaryStream(index);
                case BLOB: return rs.getBlob(index);
                case BOOLEAN: return rs.getBoolean(index);
                case DATE: return rs.getDate(index);
                case DOUBLE: return rs.getDouble(index);
                case FLOAT: return rs.getFloat(index);
                case LONG: return rs.getLong(index);
                case INT: return rs.getInt(index);
                case NULL: return null;
                case TEXT:
                case STRING:
                    return rs.getString(index);
            }
            return null;
        }
    }
}