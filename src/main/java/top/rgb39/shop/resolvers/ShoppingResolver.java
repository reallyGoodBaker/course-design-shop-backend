package top.rgb39.shop.resolvers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import top.rgb39.shop.services.ShoppingService;
import top.rgb39.shop.tools.injectable.annotation.Identifier;
import top.rgb39.shop.tools.injectable.annotation.Inject;
import top.rgb39.shop.tools.resolver.annotation.Args;
import top.rgb39.shop.tools.resolver.annotation.Filter;
import top.rgb39.shop.tools.resolver.annotation.Interface;
import top.rgb39.shop.tools.resolver.annotation.Resolver;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

@Resolver("/api/shopping")
public class ShoppingResolver {

    ShoppingService service;

    @Identifier(id = "ShoppingResolver", singleton = true)
    ShoppingResolver(
        @Inject("ShoppingService") ShoppingService service
    ) {
        this.service = service;
    }

    @Interface
    boolean doShopping(
        @Args("list") List<List<Object>> list,
        @Args("cid") String cid
    ) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return service.doShopping(list, cid);
    }

    @Filter
    boolean filter(HttpServletRequest req, HttpServletResponse res) {
        var session = req.getSession(false);
        if (session == null) return false;
        return session.getAttribute("valid").equals("_");
    }
}
