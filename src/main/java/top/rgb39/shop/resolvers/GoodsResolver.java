package top.rgb39.shop.resolvers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import top.rgb39.shop.services.GoodsService;
import top.rgb39.shop.tools.injectable.annotation.Identifier;
import top.rgb39.shop.tools.injectable.annotation.Inject;
import top.rgb39.shop.tools.resolver.annotation.Args;
import top.rgb39.shop.tools.resolver.annotation.Filter;
import top.rgb39.shop.tools.resolver.annotation.Interface;
import top.rgb39.shop.tools.resolver.annotation.Resolver;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Resolver("/api/goods")
public class GoodsResolver {

    GoodsService service;

    @Identifier(id = "GoodsResolver", singleton = true)
    GoodsResolver(
        @Inject("GoodsService") GoodsService service
    ) {
        this.service = service;
    }

    @Interface
    void add(
        @Args("id") String id,
        @Args("name") String name,
        @Args("price") double price,
        @Args("stock") double stock
    ) throws SQLException {
        service.add(id, name, (float) price, (int) stock);
    }

    @Interface
    List<?> query(
        @Args("options") Map<String, Object> options,
        @Args("fields") List<String> fields
    ) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return service.query(options, fields);
    }

    @Interface
    void delete(
        @Args("id") String id
    ) throws SQLException {
        service.delete(id);
    }

    @Interface
    void update(
        @Args("id") String id,
        @Args("name") String name,
        @Args("price") double price,
        @Args("stock") double stock
    ) throws SQLException {
        service.update(id, name, (float) price, (int) stock);
    }

    @Filter
    boolean filter(HttpServletRequest req, HttpServletResponse res) {
        var session = req.getSession(false);

        if (session == null) return false;

        return session.getAttribute("valid").equals("_");
    }

    @Interface
    List<?> matchedGoods(
        @Args("pattern") String pattern
    ) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return service.matchedGoods(pattern);
    }

}