package top.rgb39.shop.resolvers;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import top.rgb39.shop.providers.InitializeProvider;
import top.rgb39.shop.tools.injectable.Injector;

@WebServlet("/api/*")
public class Dispatch extends HttpServlet {

    InitializeProvider init;

    @Override
    public void init() {
        Class<InitializeProvider> cls = InitializeProvider.class;
        Injector.register(cls);
        try {
            init = (InitializeProvider) Injector.getInstance(cls);
            if (init != null) {
                init.start(getServletContext().getRealPath("./"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        try {
            init.handleRequest(req, resp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
