package net.codjo.tools.sqltester.batch.task;
import net.codjo.test.common.PathUtil;
import net.codjo.test.common.fixture.DirectoryFixture;
import net.codjo.test.common.fixture.Fixture;
import static net.codjo.tools.sqltester.batch.task.util.Constants.NEW_LINE;
import net.codjo.util.file.FileUtil;
import java.io.File;
import java.io.IOException;

class SqlDeliveryFixture implements Fixture {
    private DirectoryFixture directoryFixture;
    private File grantFile;
    private File revokeFile;
    private File procedureDir;
    private File tableDir;
    private File viewDir;
    private File deliveryFile;


    SqlDeliveryFixture() {
        directoryFixture = new DirectoryFixture(
              PathUtil.findTargetDirectory(getClass()).getPath() + "/tmp");
    }


    public void doSetUp() throws Exception {
        directoryFixture.doSetUp();
        File permission = new File(directoryFixture, "permission");
        permission.mkdir();
        grantFile = new File(permission, "grant.sql");
        grantFile.createNewFile();
        revokeFile = new File(permission, "revoke.sql");
        revokeFile.createNewFile();
        procedureDir = new File(directoryFixture, "procedure");
        procedureDir.mkdir();
        tableDir = new File(directoryFixture, "table");
        tableDir.mkdir();
        viewDir = new File(directoryFixture, "view");
        viewDir.mkdir();
        deliveryFile = new File(directoryFixture, "livraison-sql.txt");
        deliveryFile.createNewFile();
    }


    public void doTearDown() throws Exception {
        directoryFixture.doTearDown();
    }


    String getDeliveryFilePath() {
        if (deliveryFile == null) {
            throw new RuntimeException("You must call doSetUp() before.");
        }
        return deliveryFile.getPath();
    }


    void mockDeliveryFile(String... rows) throws IOException {
        StringBuilder script = new StringBuilder();
        for (String row : rows) {
            script.append(row).append(NEW_LINE);
        }
        FileUtil.saveContent(deliveryFile, script.toString());
    }


    void mockGrants(String... objects) throws IOException {
        StringBuilder script = new StringBuilder()
              .append("Print \"Début Création des autorisations\"").append(NEW_LINE)
              .append("go").append(NEW_LINE).append(NEW_LINE);
        for (String object : objects) {
            script.append("grant select, insert, delete, update, references on ")
                  .append(object)
                  .append(" to Maintenance,Administration,Batch").append(NEW_LINE)
                  .append("go").append(NEW_LINE);
        }
        FileUtil.saveContent(grantFile, script.toString());
    }


    void mockBadGrants(String publicObject, String allObjects) throws IOException {
        StringBuilder script = new StringBuilder()
              .append("Print \"Début Création des autorisations\"").append(NEW_LINE)
              .append("go").append(NEW_LINE).append(NEW_LINE);

        script.append("grant all on ")
              .append(allObjects)
              .append(" to Maintenance,Administration,Batch").append(NEW_LINE)
              .append("go").append(NEW_LINE);

        script.append("grant select, insert, delete, update, references on ")
              .append(publicObject)
              .append(" to Maintenance,Public,Batch").append(NEW_LINE)
              .append("go").append(NEW_LINE);
        FileUtil.saveContent(grantFile, script.toString());
    }


    void mockRevokes(String... objects) throws IOException {
        StringBuilder script = new StringBuilder()
              .append("Print \"Début de suppression des autorisations\"")
              .append("go")
              .append(NEW_LINE)
              .append(NEW_LINE);
        for (String object : objects) {
            script.append("revoke all on ")
                  .append(object)
                  .append(" to Maintenance,Administration,Batch").append(NEW_LINE)
                  .append("go").append(NEW_LINE);
        }
        FileUtil.saveContent(revokeFile, script.toString());
    }


    void mockTables(String... tables) throws IOException {
        createFile(tableDir, tables);
    }


    void mockViews(String... views) throws IOException {
        createFile(viewDir, views);
    }


    void mockDirInTables(String dirName) throws IOException {
        new File(tableDir, dirName).mkdir();
    }


    void mockDirInViews(String dirName) throws IOException {
        new File(viewDir, dirName).mkdir();
    }


    void mockProcedures(String... procedures) throws IOException {
        createFile(procedureDir, procedures);
    }


    void mockProceduresInDir(String dirName, String... procedures) throws IOException {
        File newDir = new File(procedureDir, dirName);
        newDir.mkdir();
        for (String procedure : procedures) {
            File file = new File(newDir, procedure + ".sql");
            file.createNewFile();
        }
    }


    private void createFile(File dir, String... fileNames) throws IOException {
        for (String fileName : fileNames) {
            File file = new File(dir, fileName + ".sql");
            file.createNewFile();
        }
    }
}
