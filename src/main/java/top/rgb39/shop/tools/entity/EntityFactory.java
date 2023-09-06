package top.rgb39.shop.tools.entity;

import top.rgb39.shop.tools.entity.annotation.Entity;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EntityFactory {
    static Map<Class<?>, BasicEntity> entites = new HashMap<>();
    static Map<String, BasicEntity> entityMap = new HashMap<>();

    public static BasicEntity getEntity(Class<?> cls) throws Exception {
        if (entites.containsKey(cls)) {
            BasicEntity en = entites.get(cls);

            if (!Objects.isNull(en)) {
                return en;
            }

            en = construct(cls);
            entites.replace(cls, en);
            return en;
        }

        BasicEntity en = construct(cls);
        entites.put(cls, en);
        return en;
    }

    public static BasicEntity getEntityByName(String name) {
        return entityMap.get(name);
    }

    static BasicEntity construct(Class<?> cls) throws Exception {
        Constructor<?> c = cls.getDeclaredConstructor();
        c.setAccessible(true);
        BasicEntity en = (BasicEntity) c.newInstance();
        String name = en.getClass()
                .getAnnotation(Entity.class)
                .value();
        entityMap.put(name, en);
        return en;
    }
}
