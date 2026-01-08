@echo off
setlocal

rem Set JAVA_HOME_OLD to JDK 17
set "JAVA_HOME_OLD=C:\Program Files\Java\jdk-17"

rem Override JAVA_HOME temporarily
set "JAVA_HOME=%JAVA_HOME_OLD%"
set "Path=%JAVA_HOME%\bin;%Path%"

rem Display Java version for verification
echo Using JAVA_HOME: %JAVA_HOME%
java -version

rem Run Maven with the new JAVA_HOME
mvn clean install %*

rem Restore original environment variables (only for this session)
endlocal