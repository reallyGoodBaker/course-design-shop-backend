package top.rgb39.shop.resolvers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import top.rgb39.shop.services.AdminService;
import top.rgb39.shop.tools.injectable.annotation.Identifier;
import top.rgb39.shop.tools.injectable.annotation.Inject;
import top.rgb39.shop.tools.resolver.annotation.Args;
import top.rgb39.shop.tools.resolver.annotation.Interface;
import top.rgb39.shop.tools.resolver.annotation.Request;
import top.rgb39.shop.tools.resolver.annotation.Resolver;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Objects;

@Resolver("/api/user")
public class UserResolver {

    AdminService service;

    @Identifier(id = "UserResolver", singleton = true)
    UserResolver(
        @Inject("AdminService") AdminService service
    ) {
        this.service = service;
    }

    @Interface
    boolean login(
        @Args("name") String name,
        @Args("passwd") String passwd,
        @Request HttpServletRequest req
    ) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (service.valid(name, passwd)) {
            HttpSession session = req.getSession();
            session.setAttribute("valid", "_");
            return true;
        }

        return false;
    }

    @Interface
    void logout(
        @Request HttpServletRequest req
    ) {
        HttpSession session = req.getSession(false);
        if (Objects.nonNull(session)) {
            session.removeAttribute("valid");
            session.invalidate();
        }
    }

    @Interface
    boolean isLoggedIn(
        @Request HttpServletRequest req
    ) {
        var session = req.getSession(false);
        if (session == null) return false;
        return session.getAttribute("valid").equals("_");
    }
}
