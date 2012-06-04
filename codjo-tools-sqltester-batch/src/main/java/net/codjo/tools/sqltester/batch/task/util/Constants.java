package net.codjo.tools.sqltester.batch.task.util;
/**
 *
 */
public interface Constants {
    public static final String NEW_LINE = System.getProperty("line.separator");
    public enum BaseType {
        SYBASE,
        MYSQL
    }
    public enum CheckType {
        SCRIPTS_EXISTENCE,
        GRANTS,
        MISSING_GRANT,
        UNUSED_GRANT,
        MISSING_INDEX,
        MISSING_GAP,
        MISSING_TABLES_GRANTS,
        MISSING_VIEWS_GRANTS,
        MISSING_PROCS_GRANTS,
        DEPENDENCIES,
        EXEC_SQL_FILES,
        SCRIPT_OBJECT_NAME
    }
    public enum CREATED_OBJECTS {
        PROCEDURE,
        PROC,
        TABLE,
        VIEW,
        TRIGGER,
        OTHER
    }
}
