package top.rgb39.shop.tools.resolver;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import top.rgb39.shop.tools.ClassUtils;
import top.rgb39.shop.tools.injectable.Injector;
import top.rgb39.shop.tools.resolver.annotation.Args;
import top.rgb39.shop.tools.resolver.annotation.Filter;
import top.rgb39.shop.tools.resolver.annotation.Interface;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class Resolver {

    static Gson gson = new Gson();
    static Map<String, Class<?>> resolvers = new HashMap<>();

    public static void mapAllResolvers() throws IOException, ClassNotFoundException {
        for (Class<?> cls: ClassUtils.getAllClasses()) {
            var annotation = cls.getAnnotation(top.rgb39.shop.tools.resolver.annotation.Resolver.class);
            if (Objects.isNull(annotation))
                continue;

            mapResolver(annotation.value(), cls);
        }
    }

    static void mapResolver(String path, Class<?> cls) {
        resolvers.put(path, cls);
    }

    static PrintWriter getWriter(HttpServletResponse res) throws IOException {
        res.addHeader("Access-Control-Allow-Origin", "*");
        return res.getWriter();
    }

    public static void handle(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String reqUrl = req.getRequestURI();
        Class<?> cls = resolvers.get(reqUrl);

        if (cls == null) {
            res.setStatus(404);
            getWriter(res)
                .write(gson.toJson(new Response(false, "Nonexistent URI: " + reqUrl)));
            return;
        }

        Request value = gson.fromJson(req.getReader(), Request.class);
        if (Objects.isNull(value)) {
            getWriter(res)
                .write(gson.toJson(new Response(false, null)));
            return;
        }

        Map<String, Object> data = value.data;
        Object resolver = Injector.getInstance(cls);

        if (!handleFilter(resolver, cls, req, res)) {
            getWriter(res)
                .write(gson.toJson(new Response(false, null)));
            return;
        }

        Method method = getMethodByAnnotation(cls, value.method);
        Object returnVal;
        boolean done = true;

        if (Objects.isNull(method)) {
            getWriter(res)
                .write(gson.toJson(new Response(false, "Non-existent method: " + value.method)));
            return;
        }

        method.setAccessible(true);

        try {
            returnVal = method.invoke(resolver, getArgs(method, data, req, res));
        } catch (Exception e) {
            e.printStackTrace();
            returnVal = e.getMessage();
            done = false;
        }

        String responseBody = gson.toJson(new Response(done, returnVal));

        getWriter(res)
            .write(responseBody);
    }

    static Method getMethodByAnnotation(Class<?> cls, String value) {
        for (Method method: cls.getDeclaredMethods()) {
            Interface i = method.getAnnotation(Interface.class);

            if (!Objects.isNull(i)) {
                String methodName = i.value();
                if (value.equals(
                    methodName.equals("unset") ? method.getName() : methodName
                )) {
                    return method;
                }
            }
        }

        return null;
    }

    static boolean handleFilter(Object resolver, Class<?> cls, HttpServletRequest req, HttpServletResponse res) {
        for (Method method: cls.getDeclaredMethods()) {
            var filter = method.getAnnotation(Filter.class);

            if (filter != null) {
                method.setAccessible(true);

                try {
                    Object returnVal = method.invoke(resolver, req, res);

                    if (
                        !(returnVal instanceof Boolean) ||
                        !(Boolean) returnVal
                    ) {
                        return false;
                    }
                } catch (Exception e) {
                    return false;
                }
            }
        }

        return true;
    }

    static Object[] getArgs(
        Method method,
        Map<String, Object> data,
        HttpServletRequest req,
        HttpServletResponse res
    ) {
        List<Object> list = new ArrayList<>();
        for (Parameter p: method.getParameters()) {
            Args args = p.getAnnotation(Args.class);

            if (Objects.isNull(args)) {
                if (p.getAnnotation(top.rgb39.shop.tools.resolver.annotation.Request.class) != null) {
                    list.add(req);
                    continue;
                }

                if (p.getAnnotation(top.rgb39.shop.tools.resolver.annotation.Response.class) != null) {
                    list.add(res);
                    continue;
                }

                list.add(null);
                continue;
            }

            list.add(data.get(args.value()));
        }

        return list.toArray();
    }
}

class Request {
    public String method;
    public Map<String, Object> data;
}

class Response {
    public boolean done;
    public Object data;

    Response(boolean done, Object data) {
        this.data = data;
        this.done = done;
    }
}