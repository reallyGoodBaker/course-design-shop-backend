package top.rgb39.shop.resolvers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import top.rgb39.shop.services.CustomerService;
import top.rgb39.shop.tools.injectable.annotation.Identifier;
import top.rgb39.shop.tools.injectable.annotation.Inject;
import top.rgb39.shop.tools.resolver.annotation.Args;
import top.rgb39.shop.tools.resolver.annotation.Filter;
import top.rgb39.shop.tools.resolver.annotation.Interface;
import top.rgb39.shop.tools.resolver.annotation.Resolver;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

@Resolver("/api/customer")
public class CustomerResolver {

    CustomerService service;

    @Identifier(id = "CustomerResolver", singleton = true)
    CustomerResolver(
        @Inject("CustomerService") CustomerService service
    ) {
        this.service = service;
    }

    @Interface
    boolean exists(
        @Args("id") String id
    ) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return service.exists(id);
    }

    @Filter
    boolean filter(HttpServletRequest req, HttpServletResponse res) {
        var session = req.getSession(false);
        if (session == null) return false;
        return session.getAttribute("valid").equals("_");
    }
}
