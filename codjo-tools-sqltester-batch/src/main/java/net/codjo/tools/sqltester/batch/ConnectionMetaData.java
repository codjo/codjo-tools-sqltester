package net.codjo.tools.sqltester.batch;
import net.codjo.tools.sqltester.batch.task.util.TaskUtil;
import java.io.File;
/**
 * Détermination des paramétres de connexion de la base développeur
 */
public class ConnectionMetaData {
    private static final String USER_HOME = System.getProperty("user.home");
    private static final String SYBASE_DRIVER = "com.sybase.jdbc2.jdbc.SybDriver";
    private static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    private String databaseSettings;
    private String catalog;
    private String base;
    private String user;
    private String password;
    private String port;
    private String server;
    private String databaseType;


    public ConnectionMetaData() {
        this(USER_HOME + "\\.m2\\settings.xml");
    }


    public ConnectionMetaData(String settingsPath) {
        databaseSettings = getDatabaseSettings(settingsPath);
        catalog = getDatabaseParameter("databaseCatalog");
        base = getDatabaseParameter("databaseBase");
        user = getDatabaseParameter("databaseUser");
        password = getDatabaseParameter("databasePassword");
        port = getDatabaseParameter("databasePort");
        server = getDatabaseParameter("databaseServer");
        databaseType = getDatabaseParameter("databaseType");
    }


    private String getDatabaseParameter(String parameter) {
        int startIndex = databaseSettings.indexOf("<" + parameter + ">") + ("<" + parameter + ">").length();
        return databaseSettings.substring(startIndex, databaseSettings.indexOf("<", startIndex));
    }


    String getDatabaseSettings(String settingsPath) {
        File file = new File(settingsPath);
        String settingsContent = TaskUtil.getContentOfFile(file);
        int baseSettings = settingsContent.indexOf("<id>database-developer</id>");
        int startIndex = settingsContent.indexOf("<properties>", baseSettings) + "<properties>".length() + 2;
        int endIndex = settingsContent.indexOf("</properties>", baseSettings);
        return settingsContent.substring(startIndex, endIndex).replaceAll(" ", "");
    }


    public String getCatalog() {
        return catalog;
    }


    public String getBase() {
        return base;
    }


    public String getUser() {
        return user;
    }


    public String getPassword() {
        return password;
    }


    public String getPort() {
        return port;
    }


    public String getServer() {
        return server;
    }


    public String getJdbcUrl() {
        if ("sybase".equalsIgnoreCase(databaseType)) {
            return "jdbc:sybase:Tds:" + server + ":" + port;
        }
        else if ("mysql".equalsIgnoreCase(databaseType)) {
            return "jdbc:mysql://" + server + ":" + port;
        }
        else {
            return "jdbc:sybase:Tds:" + server + ":" + port;
        }
    }


    public String getDriver() {
        if ("sybase".equalsIgnoreCase(databaseType)) {
            return SYBASE_DRIVER;
        }
        else if ("mysql".equalsIgnoreCase(databaseType)) {
            return MYSQL_DRIVER;
        }
        else {
            return SYBASE_DRIVER;
        }
    }


    public String getDatabaseType() {
        if (databaseType == null) {
            return "sybase";
        }
        return databaseType;
    }


    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }


    public void setBase(String base) {
        this.base = base;
    }
}

