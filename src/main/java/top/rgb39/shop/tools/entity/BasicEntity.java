package top.rgb39.shop.tools.entity;

import top.rgb39.shop.tools.entity.annotation.Entity;
import top.rgb39.shop.tools.entity.annotation.Field;
import top.rgb39.shop.tools.entity.annotation.FieldTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicEntity {

    public static class FieldValue {
        public String field;
        public FieldTypes type;
        public int index;
        public boolean primaryKey;

        FieldValue(String field, FieldTypes type, int index, boolean primaryKey) {
            this.field = field;
            this.type = type;
            this.index = index;
            this.primaryKey = primaryKey;
        }
    }

    public List<FieldValue> getEntityFields() {
        List<FieldValue> fields = new ArrayList<>();
        int i = 0;
        for (java.lang.reflect.Field f: this.getClass().getFields()) {
            Field _f = f.getAnnotation(Field.class);
            String field = _f.field();
            FieldTypes type = _f.type();

            if (field.equals("unset")) {
                field = f.getName();
            }

            fields.add(new FieldValue(
                field,
                type,
                i++,
                _f.primaryKey()
            ));
        }

        return fields;
    }

    public String getEntityName() {
        return getClass()
                .getAnnotation(Entity.class)
                .value();
    }

    public Map<String, FieldValue> getEntityFieldMapping() {
        Map<String, FieldValue> fieldMapping = new HashMap<>();
        int i = 0;
        for (java.lang.reflect.Field f: this.getClass().getFields()) {
            Field _f = f.getAnnotation(Field.class);
            String field = _f.field();
            FieldTypes type = _f.type();

            if (field.equals("unset")) {
                field = f.getName();
            }

            fieldMapping.put(field, new FieldValue(
                field,
                type,
                i++,
                _f.primaryKey()
            ));
        }

        return fieldMapping;
    }
}
