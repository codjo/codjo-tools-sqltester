package net.codjo.tools.sqltester.batch;
import static net.codjo.tools.sqltester.batch.task.util.Constants.NEW_LINE;
import net.codjo.tools.sqltester.batch.ConnectionMetaData;
import junit.framework.TestCase;
/**
 *
 */
public class ConnectionMetaDataTest extends TestCase {
    private ConnectionMetaData metadata;
    private String settingsPath;


    public void test_getDatabaseSettings() throws Exception {
        assertEquals("<databaseServer>ai-lib12</databaseServer>" + NEW_LINE
                     + "<databasePort>34100</databasePort>" + NEW_LINE
                     + "<databaseCatalog>LIB_INT</databaseCatalog>" + NEW_LINE
                     + "<databaseBase>GDO_DEV</databaseBase>" + NEW_LINE
                     + "<databaseUser>LIB_INT_dbo</databaseUser>" + NEW_LINE
                     + "<databasePassword>LIB_INT_dbo</databasePassword>" + NEW_LINE
                     + "<databaseJdbcUrl>jdbc:sybase:Tds:ai-lib12:34100</databaseJdbcUrl>" + NEW_LINE
                     + "<databaseDriver>com.sybase.jdbc2.jdbc.SybDriver</databaseDriver>" + NEW_LINE
                     + "<databaseType>sybase</databaseType>" + NEW_LINE,
                     metadata.getDatabaseSettings(settingsPath));
    }


    public void test_getCatalog() {
        assertEquals("LIB_INT", metadata.getCatalog());
    }


    public void test_getBase() {
        assertEquals("GDO_DEV", metadata.getBase());
    }


    public void test_getUser() {
        assertEquals("LIB_INT_dbo", metadata.getUser());
    }


    public void test_getPassword() {
        assertEquals("LIB_INT_dbo", metadata.getPassword());
    }


    public void test_getPort() {
        assertEquals("34100", metadata.getPort());
    }


    public void test_getServer() {
        assertEquals("ai-lib12", metadata.getServer());
    }


    public void test_getJdbcUrl() {
        assertEquals("jdbc:sybase:Tds:ai-lib12:34100", metadata.getJdbcUrl());
    }


    public void test_getDriver() {
        assertEquals("com.sybase.jdbc2.jdbc.SybDriver", metadata.getDriver());
    }


    public void test_getDatabaseType() {
        assertEquals("sybase", metadata.getDatabaseType());
    }


    @Override
    protected void setUp() throws Exception {
        settingsPath = getClass().getResource("settings-sybase.xml").getPath();
        metadata = new ConnectionMetaData(settingsPath);
    }
}
