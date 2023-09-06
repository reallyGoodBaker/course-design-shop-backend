package top.rgb39.shop.tools;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ClassUtils {
    public static List<Class<?>> getAllClasses() throws IOException, ClassNotFoundException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String classRoot = Objects.requireNonNull(loader.getResource("/")).getPath();
        Package[] pkgs = loader.getDefinedPackages();
        List<Class<?>> classes = new ArrayList<>();

        for (Package pkg : pkgs) {
            Enumeration<URL> urls = loader.getResources(pkg.getName().replace(".", "/"));

            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url.getProtocol().equals("file")) {
                    String classesPath = url.getPath();
                    File f = new File(classesPath);

                    for (File file: Objects.requireNonNull(f.listFiles())) {
                        if (file.getPath().endsWith(".class")) {
                            classes.add(getClassFromFile(file, classRoot));
                        }
                    }
                }
            }
        }

        return classes;
    }

    static Class<?> getClassFromFile(File file, String root) throws ClassNotFoundException {
        String filePath = file.getPath();
        String name = filePath
                .substring(0, filePath.length() - 6)
                .replace("\\", "/")
                .replace(root.substring(1), "")
                .replace("/", ".");

        return Class.forName(name);
    }

}
