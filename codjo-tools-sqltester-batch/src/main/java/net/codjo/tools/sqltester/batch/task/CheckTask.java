package net.codjo.tools.sqltester.batch.task;
import org.apache.tools.ant.BuildException;
public interface CheckTask {
    String getTaskName();


    void execute() throws BuildException;
}
