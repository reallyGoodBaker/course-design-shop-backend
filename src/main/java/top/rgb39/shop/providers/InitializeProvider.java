package top.rgb39.shop.providers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import top.rgb39.shop.tools.injectable.Injector;
import top.rgb39.shop.tools.injectable.annotation.Identifier;
import top.rgb39.shop.tools.resolver.Resolver;

public class InitializeProvider {

    ConfigProvider config;

    @Identifier(id = "InitializeProvider")
    InitializeProvider() {}

    public void start(String rootPath) throws Exception {
        config = (ConfigProvider) Injector.getInstance(ConfigProvider.class, rootPath + "config.json");

        registerAllInjectables();
        Resolver.mapAllResolvers();
    }

    void registerAllInjectables() throws ClassNotFoundException {
        String root = config.root();
        for (String injectable : config.injectables()) {
            Injector.register(Class.forName(String.format(
                "%s.%s",
                root,
                injectable
            )));
        }
    }

    public void handleRequest(
        HttpServletRequest req,
        HttpServletResponse res
    ) throws Exception {
        Resolver.handle(req, res);
    }
}
