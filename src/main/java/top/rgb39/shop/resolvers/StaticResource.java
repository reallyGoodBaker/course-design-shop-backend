package top.rgb39.shop.resolvers;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import top.rgb39.shop.entities.CustomerEntity;
import top.rgb39.shop.entities.DealEntity;
import top.rgb39.shop.providers.DatabaseProvider;
import top.rgb39.shop.tools.entity.BasicEntity;

@WebServlet("/*")
public class StaticResource extends HttpServlet {

    static Map<String, String> mimeTypes = new HashMap<>();

    static {
        mimeTypes.put("js", "application/javascript");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String reqUrl = req.getRequestURI();
        reqUrl = reqUrl.equals("/") ? reqUrl + "index.html" : reqUrl;

        String[] parts = reqUrl.split("\\.");
        String[] paths = reqUrl.split("/");

        String suffix = parts[parts.length - 1];
        String fileName = paths[paths.length - 1];

        String mimeType = Files.probeContentType(Path.of(fileName));

        if (mimeTypes.containsKey(suffix)) {
            mimeType = mimeTypes.get(suffix);
        }

        String frontendPath = getServletContext().getRealPath("/shop-frontend/dist" + reqUrl);

        res.addHeader("Content-Type", mimeType);

        OutputStream output = res.getOutputStream();
        InputStream input = new FileInputStream(frontendPath);
        input.transferTo(output);

    }
}