package me.vanjavk.dal.sql;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import me.vanjavk.singleton.Configuration;

import javax.sql.DataSource;

public final class DataSourceSingleton {

    private DataSourceSingleton() {}

    private static DataSource instance;
    
    public static DataSource getInstance() {
        if (instance == null)  {
            instance = createInstance();
        }
        return instance;
    }
    
    private static DataSource createInstance() {
        SQLServerDataSource dataSource = new SQLServerDataSource();
        dataSource.setServerName(Configuration.SERVER_NAME);
        dataSource.setDatabaseName(Configuration.DATABASE_NAME);
        dataSource.setUser(Configuration.USER);
        dataSource.setPassword(Configuration.PASSWORD);

        return dataSource;
    }    
}
