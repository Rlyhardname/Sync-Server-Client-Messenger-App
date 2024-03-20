package server.dao;

import javax.sql.DataSource;

public class DataSourcePool {
    private static DataSource dataSource;

    public static DataSource instanceOf(DataSource dSource) {
        if (dataSource == null) {
            dataSource = dSource;
        }
        return dataSource;
    }

    public static DataSource instanceOf() {
        return dataSource;
    }

    private static void resetSource() {
        dataSource = null;
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}