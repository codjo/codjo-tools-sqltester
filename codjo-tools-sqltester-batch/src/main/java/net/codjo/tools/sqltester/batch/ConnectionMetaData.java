package net.codjo.tools.sqltester.batch;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
/**
 * Détermination des paramétres de connexion de la base développeur
 */
public class ConnectionMetaData {
    private static final String SYBASE_DRIVER = "com.sybase.jdbc2.jdbc.SybDriver";
    private static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    private static final String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";
    private Properties databaseProperties;
    private String catalog;
    private String base;


    public ConnectionMetaData(String deliverySqlFilePath) throws IOException {
        File srcSqlDir = new File(deliverySqlFilePath).getParentFile();
        databaseProperties
              = loadProperties(srcSqlDir.getParentFile().getParentFile().getParentFile()
                               + "\\target\\test-classes\\database.properties");
    }


    public ConnectionMetaData(String deliverySqlFilePath, String deliveryFileName) throws IOException {
        File deliverySqlFile = new File(deliverySqlFilePath, deliveryFileName);
        databaseProperties = loadProperties(deliverySqlFile.getPath());
    }


    private Properties loadProperties(String configurationFile) throws IOException {
        Properties properties = new Properties();
        FileInputStream stream = new FileInputStream(configurationFile);
        try {
            properties.load(stream);
        }
        finally {
            stream.close();
        }
        return properties;
    }


    public String getCatalog() {
        return catalog == null ? (String)databaseProperties.get("database.catalog") : catalog;
    }


    public String getBase() {
        return base == null ? (String)databaseProperties.get("database.base") : base;
    }


    public String getUser() {
        return (String)databaseProperties.get("database.user");
    }


    public String getPassword() {
        return (String)databaseProperties.get("database.password");
    }


    public String getPort() {
        return (String)databaseProperties.get("database.port");
    }


    public String getServer() {
        return (String)databaseProperties.get("database.hostname");
    }


    public String getDatabaseType() {
        return (String)databaseProperties.get("database.engine");
    }


    public String getJdbcUrl() {
        if ("sybase".equalsIgnoreCase(getDatabaseType())) {
            return "jdbc:sybase:Tds:" + getServer() + ":" + getPort();
        }
        else if ("mysql".equalsIgnoreCase(getDatabaseType())) {
            return "jdbc:mysql://" + getServer() + ":" + getPort();
        }
        else if ("oracle".equalsIgnoreCase(getDatabaseType())) {
            return "jdbc:oracle:thin:@" + getServer() + ":" + getPort() + ":" + getBase();
        }
        else {
            return "jdbc:sybase:Tds:" + getServer() + ":" + getPort();
        }
    }


    public String getDriver() {
        if ("sybase".equalsIgnoreCase(getDatabaseType())) {
            return SYBASE_DRIVER;
        }
        else if ("mysql".equalsIgnoreCase(getDatabaseType())) {
            return MYSQL_DRIVER;
        }
        else if ("oracle".equalsIgnoreCase(getDatabaseType())) {
            return ORACLE_DRIVER;
        }
        else {
            return SYBASE_DRIVER;
        }
    }


    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }


    public void setBase(String base) {
        this.base = base;
    }
}

