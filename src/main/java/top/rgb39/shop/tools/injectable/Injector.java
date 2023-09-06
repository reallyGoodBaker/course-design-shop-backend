package top.rgb39.shop.tools.injectable;

import top.rgb39.shop.tools.injectable.annotation.Identifier;
import top.rgb39.shop.tools.injectable.annotation.Inject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;

public class Injector {

    static class Identified {

        Identifier id;
        Constructor<?> ctor;

        Identified(Identifier id, Constructor<?> ctor) {
            this.id = id;
            this.ctor = ctor;
        }
    }

    static Map<String, Class<?>> classes = new HashMap<>();
    static Map<String, Object> singleton = new HashMap<>();

    public static Object getInstance0(Class<?> cls, Object[] args) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        var identified = getIdentified(cls);
        if (Objects.isNull(identified))
            return null;

        var identifier = identified.id;
        String id = identifier.id();

        if (!identifier.singleton()) {
            return newInstance(cls, args);
        }

        Object inst = singleton.get(id);
        if (Objects.isNull(inst)) {
            inst = newInstance(cls, args);
            singleton.put(id, inst);
        }

        return inst;
    }

    public static Object newInstance(Class<?> cls, Object[] args) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Identified identified = getIdentified(cls);

        if (identified == null) {
            return null;
        }

        Constructor<?> ctor = identified.ctor;

        ctor.setAccessible(true);
        return ctor.newInstance(args);
    }

    public static Object getInstance0(String id, Object[] args) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> cls = classes.get(id);

        if (cls == null) {
            return null;
        }

        return getInstance0(cls, args);
    }

    public static void register(String id, Class<?> cls) {
        classes.put(id, cls);
    }

    public static void register(Class<?> cls) {
        Identifier id = Objects.requireNonNull(getIdentified(cls)).id;
        register(id.id(), cls);
    }

    static Identified getIdentified(Class<?> cls) {
        for (Constructor<?> c: cls.getDeclaredConstructors()) {
            Identifier identifier = c.getAnnotation(Identifier.class);
            if (Objects.isNull(identifier)) {
                continue;
            }

            return new Identified(identifier, c);
        }

        return null;
    }

    public static List<Object> buildDependencies(Class<?> cls) throws Exception {
        var identified = getIdentified(cls);

        if (Objects.isNull(identified)) {
            return null;
        }

        var identifier = identified.id;
        var ctor = identified.ctor;

        return buildDependencies0(identifier.id(), ctor, new HashMap<>());
    }

    static List<Object> buildDependencies0(
        String rootID,
        Constructor<?> ctor,
        Map<String, Object> modules
    ) throws Exception {
        List<Inject> depInjects = getInjectsFromCtor(ctor);
        List<Object> deps = new ArrayList<>();

        for (Inject inject: depInjects) {
            if (Objects.isNull(inject)) {
                deps.add(null);
                continue;
            }

            if (inject.value().equals(rootID))
                throw new Exception("循环依赖");

            Class<?> cls = classes.get(inject.value());
            if (Objects.isNull(cls)) {
                deps.add(null);
                continue;
            }

            String depId = inject.value();
            if (modules.containsKey(depId)) {
                deps.add(modules.get(depId));
                continue;
            }

            if (singleton.containsKey(depId)) {
                Object dep = singleton.get(depId);
                modules.put(depId, dep);
                deps.add(dep);
                continue;
            }

            Identified identified = getIdentified(cls);
            if (!Objects.isNull(identified)) {
                Object dep = getInstance0(cls, buildDependencies0(
                    rootID, identified.ctor, modules
                ).toArray());

                modules.put(depId, dep);
                deps.add(dep);
            }
        }

        return deps;
    }

    static List<Inject> getInjectsFromCtor(Constructor<?> ctor) {
        List<Inject> injects = new ArrayList<>();
        for (Parameter param: ctor.getParameters()) {
            Inject inject = param.getAnnotation(Inject.class);
            if (Objects.isNull(inject)) {
                injects.add(null);
                continue;
            }

            injects.add(inject);
        }

        return injects;
    }


    public static Object getInstance(Class<?> cls, Object... args) throws Exception {
        List<Object> deps = buildDependencies(cls);
        if (Objects.isNull(deps)) {
            return null;
        }

        int argIndex = 0;
        for (int i = 0; i < deps.size(); i++) {
            Object dep = deps.get(i);
            Object arg = argIndex < args.length
                    ? args[argIndex++]
                    : null;
            if (Objects.isNull(dep)) {
                deps.set(i, arg);
            }
        }

        return getInstance0(cls, deps.toArray());
    }
}
