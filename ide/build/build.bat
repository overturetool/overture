@echo off
echo Building overture

set BUILD_DIR=%CD%\build

REM -Dbase=C:\Users\kela\Downloads\eclipse352
REM -Dbaseos=win32
REM -Dbasews=win32
REM -Dbasearch=x86
REM -DbuildDirectory=C:/overture.build2


REM -DeclipseLocation=C:\Users\kela\Downloads\eclipse352
REM -DequinoxLauncherPluginVersion=1.0.201.R35x_v20090715
REM -DpdeBuildPluginVersion=3.5.2.R35x_20100114



set ECLIPSE_HOME="C:\Users\kela\Downloads\eclipse352"
set BASE=%ECLIPSE_HOME%
set BUILD_XML_PATH="C:\overture\overturesvn\ide\build\"
set equinoxLauncherPluginVersion="1.0.201.R35x_v20090715"
set pdeBuildPluginVersion="3.5.2.R35x_20100114"
set BASEOS=win32
set BASEWS=win32
set BASEARCH=x86


echo Changing to build.xml directory

cd %BUILD_XML_PATH%

echo Starting Eclipse ANT runner

set LAUNCHER_PLUGIN=%ECLIPSE_HOME%\plugins\org.eclipse.equinox.launcher_%equinoxLauncherPluginVersion%.jar

java -cp %LAUNCHER_PLUGIN% org.eclipse.equinox.launcher.Main -application org.eclipse.ant.core.antRunner -buildfile build.xml -Dbase=%BASE% -DeclipseLocation=%ECLIPSE_HOME% -DequinoxLauncherPluginVersion=%equinoxLauncherPluginVersion% -DbuildDirectory=%BUILD_DIR% -DpdeBuildPluginVersion=%pdeBuildPluginVersion% -Dbaseos=%BASEOS% -Dbasews=%BASEWS% -Dbasearch=%BASEARCH%

cd %BUILD_DIR%
cd ..

echo Done

