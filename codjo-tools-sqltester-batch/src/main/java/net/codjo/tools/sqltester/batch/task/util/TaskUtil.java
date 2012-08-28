package net.codjo.tools.sqltester.batch.task.util;

import net.codjo.tools.sqltester.batch.ConnectionMetaData;
import net.codjo.tools.sqltester.batch.task.util.Constants.CREATED_OBJECTS;
import static net.codjo.tools.sqltester.batch.task.util.Constants.NEW_LINE;
import net.codjo.util.file.FileUtil;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FileSet;

/**
 *
 */
public class TaskUtil {
    private static final int FLAGS = Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE;


    private TaskUtil() {
    }


    public static String getContentOfFile(File file) throws BuildException {
        try {
            return FileUtil.loadContent(file);
        }
        catch (IOException e) {
            throw new BuildException("Impossible de lire le fichier " + file.getAbsolutePath(), e);
        }
    }


    public static String getContentOfFile(String filePath) throws BuildException {
        return getContentOfFile(new File(filePath));
    }


    public static String findUnexistingFilesIn(String filePath) throws BuildException {
        StringBuilder filesNotFound = new StringBuilder();

        File file = new File(filePath);
        String contentOfFile = getContentOfFile(file);
        if (contentOfFile == null) {
            return "";
        }
        String[] scripts = contentOfFile.split(NEW_LINE);
        for (String script1 : scripts) {
            String script = script1.trim();
            File parentFile = file.getParentFile();
            if (!new File(parentFile, script).exists()) {
                if (filesNotFound.length() == 0) {
                    filesNotFound.append("Les fichiers suivants sont introuvables :").append(NEW_LINE);
                }
                filesNotFound.append(script.trim()).append(NEW_LINE);
            }
        }
        return filesNotFound.toString();
    }


    public static String removeCommentBlocks(String toEvaluate) {
        Matcher matcher;
        Pattern pattern;

        // Suppression des lignes de commentaire : --
        pattern = Pattern.compile("-{2,}?.*?$", FLAGS);
        matcher = pattern.matcher(toEvaluate);
        String noComment = matcher.replaceAll("");

        // Suppression des blocks de commentaire : /* ... */
        String startComment = "/\\*";
        String endComment = "\\*/";

        pattern = Pattern.compile(startComment + ".*?" + endComment, FLAGS);
        matcher = pattern.matcher(noComment);
        noComment = matcher.replaceAll("");
        return noComment;
    }


    public static String getObjectName(String script) {
        return script.substring(script.lastIndexOf("/") + 1, script.lastIndexOf("."));
    }


    public static CREATED_OBJECTS getObjectType(String scriptPath) {
        if (scriptPath.indexOf("/") != scriptPath.lastIndexOf("/")) {
            return CREATED_OBJECTS.OTHER;
        }

        String scriptType = scriptPath.substring(0, scriptPath.indexOf("/"));
        if (scriptType.toUpperCase().equals(CREATED_OBJECTS.PROCEDURE.toString())) {
            return CREATED_OBJECTS.PROCEDURE;
        }
        else if (scriptType.toUpperCase().equals(CREATED_OBJECTS.PROC.toString())) {
            return CREATED_OBJECTS.PROC;
        }
        else if (scriptType.toUpperCase().equals(CREATED_OBJECTS.TABLE.toString())) {
            return CREATED_OBJECTS.TABLE;
        }
        else if (scriptType.toUpperCase().equals(CREATED_OBJECTS.VIEW.toString())) {
            return CREATED_OBJECTS.VIEW;
        }
        else if (scriptType.toUpperCase().equals(CREATED_OBJECTS.TRIGGER.toString())) {
            return CREATED_OBJECTS.TRIGGER;
        }
        return CREATED_OBJECTS.OTHER;
    }


    public static boolean doesScriptContainsCreateObjectName(String scriptContent,
                                                             CREATED_OBJECTS objectType,
                                                             String objectName) {
        String upperCaseScriptContent = scriptContent.toUpperCase();
        String upperCaseObjectName = objectName.toUpperCase();
        switch (objectType) {
            case TABLE:
                return upperCaseScriptContent.contains("CREATE " + objectType + " " + upperCaseObjectName);
            case PROCEDURE:
            case PROC:
                return upperCaseScriptContent.contains("CREATE " + objectType + " " + upperCaseObjectName)
                       || upperCaseScriptContent.contains(
                      "CREATE " + CREATED_OBJECTS.PROC + " " + upperCaseObjectName);
            case VIEW:
                return upperCaseScriptContent.contains("CREATE " + objectType + " " + upperCaseObjectName);
            case TRIGGER:
                return upperCaseScriptContent.contains("CREATE " + objectType + " " + upperCaseObjectName);
            case OTHER:
                return true;
            default:
                return false;
        }
    }


    public static boolean doesPathContainDirectory(String pathToEvaluate, String directoryToFind) {
        Pattern pattern = Pattern.compile(".*/" + directoryToFind + "/.*", FLAGS);
        Matcher matcher = pattern.matcher(pathToEvaluate);
        return matcher.matches();
    }


    public static boolean doesContainDropObject(String toEvaluate, String objectToFind) {
        Pattern pattern = Pattern.compile(".*/drop/" + objectToFind + "\\..*", FLAGS);
        Matcher matcher = pattern.matcher(toEvaluate);
        return matcher.matches();
    }


    public static boolean doesPathStartWithDirectory(String toEvaluate, String toFind) {
        Pattern pattern = Pattern.compile(".*^" + toFind + "/.*", FLAGS);
        Matcher matcher = pattern.matcher(toEvaluate);
        return matcher.matches();
    }


    public static boolean doesPathContainTable(String toEvaluate) {
        return TaskUtil.doesPathStartWithDirectory(toEvaluate, "table")
               && !TaskUtil.doesPathContainDirectory(toEvaluate, "drop")
               && !TaskUtil.doesPathContainDirectory(toEvaluate, "alter")
               && !TaskUtil.doesContainString(toEvaluate, "-gap")
               && !TaskUtil.doesContainString(toEvaluate, "-sequence")
               && !TaskUtil.doesContainString(toEvaluate, "_SEQ_I");
    }


    public static boolean doesPathContainView(String toEvaluate) {
        return TaskUtil.doesPathStartWithDirectory(toEvaluate, "view")
               && !TaskUtil.doesPathContainDirectory(toEvaluate, "drop");
    }


    public static boolean doesPathContainProc(String toEvaluate) {
        return TaskUtil.doesPathStartWithDirectory(toEvaluate, "procedure")
               && !TaskUtil.doesPathContainDirectory(toEvaluate, "drop");
    }


    private static boolean doesContainString(String toEvaluate, String toFind) {
        Pattern pattern = Pattern.compile(".*" + toFind + ".*", FLAGS);
        Matcher matcher = pattern.matcher(toEvaluate);
        return matcher.matches();
    }


    public static String assemblySql(String deliverySqlFilePath) {
        File srcSqlDir = new File(deliverySqlFilePath).getParentFile();
        File targetSqlDir = new File(srcSqlDir.getParentFile().getParentFile().getParentFile(), "target/sql");
        String from = srcSqlDir.getPath();
        String to = targetSqlDir.getPath();
        Project copyProject = new Project();
        Copy copyTask = new Copy();
        FileSet fileSet = new FileSet();
        fileSet.setDir(new File(from));
        fileSet.setExcludes("**/.svn");
        copyTask.addFileset(fileSet);
        copyTask.setTodir(new File(to));
        copyTask.setOverwrite(true);
        copyTask.setProject(copyProject);
        copyProject.init();
        copyTask.execute();
        return to + "/" + new File(deliverySqlFilePath).getName();
    }


    public static Connection builConnection(ConnectionMetaData metadata)
          throws SQLException, ClassNotFoundException {
        Properties props = new Properties();
        props.put("user", metadata.getUser());
        props.put("password", metadata.getPassword());

        Class.forName(metadata.getDriver());
        Connection connection = DriverManager.getConnection(metadata.getJdbcUrl(), props);
        connection.setCatalog(metadata.getCatalog());

        return connection;
    }


    public static void closeConnection(Connection connection) throws SQLException {
        connection.close();
    }
}
