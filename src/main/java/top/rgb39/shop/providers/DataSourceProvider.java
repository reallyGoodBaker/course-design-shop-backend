package top.rgb39.shop.providers;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import top.rgb39.shop.tools.injectable.annotation.Identifier;
import top.rgb39.shop.tools.injectable.annotation.Inject;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class DataSourceProvider {
    ComboPooledDataSource ds = null;

    public ComboPooledDataSource getDataSource() {
        return ds;
    }

    public Connection getConnection() throws SQLException, PropertyVetoException {
        if (ds == null) {
            ds = getDataSource();
        }

        return ds.getConnection();
    }

    @Identifier(id = "DataSourceProvider", singleton = true)
    DataSourceProvider(
        @Inject("ConfigProvider") ConfigProvider config
    ) throws PropertyVetoException {
        DatabaseSettings settings = config.database();

        ds = new ComboPooledDataSource();
        ds.setDriverClass(settings.jdbcDriver);
        ds.setJdbcUrl(settings.jdbcUrl);
        ds.setUser(settings.username);
        ds.setPassword(settings.password);
    }
}
