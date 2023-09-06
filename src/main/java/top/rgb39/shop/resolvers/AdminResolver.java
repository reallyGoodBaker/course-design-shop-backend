package top.rgb39.shop.resolvers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import top.rgb39.shop.providers.DatabaseProvider;
import top.rgb39.shop.services.AdminService;
import top.rgb39.shop.tools.injectable.annotation.Identifier;
import top.rgb39.shop.tools.injectable.annotation.Inject;
import top.rgb39.shop.tools.resolver.annotation.Args;
import top.rgb39.shop.tools.resolver.annotation.Filter;
import top.rgb39.shop.tools.resolver.annotation.Interface;
import top.rgb39.shop.tools.resolver.annotation.Resolver;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

@Resolver("/api/admin")
public class AdminResolver {

    AdminService service;
    DatabaseProvider db;

    @Identifier(id = "AdminResolver", singleton = true)
    AdminResolver(
        @Inject("AdminService") AdminService service,
        @Inject("DatabaseProvider") DatabaseProvider db
    ) {
        this.service = service;
        this.db = db;
    }

    @Interface
    String hello(
        @Args("name") String name,
        @Args("int") double i
    ) {
        return "Hello " + name + i;
    }

    @Interface
    void add(
        @Args("name") String name,
        @Args("passwd") String passwd
    ) throws SQLException {
        service.add(name, passwd);
    }

    @Filter
    boolean filter(HttpServletRequest req, HttpServletResponse res) {
        var session = req.getSession(false);

        if (session == null) return false;

        return session.getAttribute("valid").equals("_");
    }

    @Interface
    List<String> names() throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return service.names();
    }

    @Interface
    void delete(
        @Args("name") String name
    ) throws SQLException {
        service.delete(name);
    }

    @Interface
    void update(
        @Args("name") String name,
        @Args("passwd") String passwd
    ) throws SQLException {
        service.update(name, passwd);
    }
}
