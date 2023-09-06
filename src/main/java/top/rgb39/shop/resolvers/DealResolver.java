package top.rgb39.shop.resolvers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import top.rgb39.shop.services.DealService;
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

@Resolver("/api/deal")
public class DealResolver {

    DealService service;

    @Identifier(id = "DealResolver", singleton = true)
    DealResolver(
        @Inject("DealService") DealService service
    ) {
        this.service = service;
    }

    @Filter
    boolean filter(HttpServletRequest req, HttpServletResponse res) {
        var session = req.getSession(false);
        if (session == null) return false;
        return session.getAttribute("valid").equals("_");
    }

    @Interface
    void delete(
        @Args("id") String id
    ) throws SQLException {
        service.delete(id);
    }

    @Interface
    List<?> query(
        @Args("conditions") Map<String, Object> conditions,
        @Args("fields") List<String> fields,
        @Args("options") Map<String, Object> options
    ) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return service.query(conditions, fields, options);
    }

}
