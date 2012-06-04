@set JAVA_HOME=c:\jdk\jdk16
@set PATH=%JAVA_HOME%\bin;%PATH%

@if "%1" == "" goto FileMissing

@set CLASSPATH=%CLASSPATH%;C:\Dev\platform\tools\codjo-tools-sqltester
@java -jar C:\Dev\platform\tools\codjo-tools-sqltester\codjo-tools-sqltester-batch-@project.version@.jar %1
@goto End

:FileMissing
@echo ERROR : Le chemin du fichier livraison-sql.txt est manquant
@goto PrintUsage

:PrintUsage
@echo -----------------------------------------------------
@echo USAGE :
@echo.
@echo    SqlTester [file name]
@echo      e.g. SqlTester C:\Dev\projects\mint\mint-sql\src\main\sql\livraison-sql.txt
@echo.
@echo -----------------------------------------------------

:End
