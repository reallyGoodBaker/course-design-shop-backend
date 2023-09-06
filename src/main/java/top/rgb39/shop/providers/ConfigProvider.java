package top.rgb39.shop.providers;

import com.google.gson.Gson;
import top.rgb39.shop.tools.injectable.annotation.Identifier;

import java.io.FileNotFoundException;
import java.io.FileReader;

class DatabaseSettings {
    public String jdbcDriver;
    public String jdbcUrl;
    public String username;
    public String password;
    public String databaseName;
}

class Config {
    public DatabaseSettings database;
    public String root;
    public String[] entities;
    public String[] injectables;
}

public class ConfigProvider {

    static Gson gson = new Gson();

    Config config;

    @Identifier(id = "ConfigProvider", singleton = true)
    ConfigProvider(String path) throws FileNotFoundException {
        config = gson.fromJson(new FileReader(path), Config.class);
    }

    public String root() {
        return config.root;
    }

    public String[] entities() {
        return config.entities;
    }

    public String[] injectables() {
        return config.injectables;
    }

    public DatabaseSettings database() {
        return config.database;
    }
}
