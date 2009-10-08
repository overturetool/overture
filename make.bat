@echo off
if (%1) == (install) goto INSTALL
if (%1) == (clean) goto CLEAN

goto END


:INSTALL
echo install

TITLE Making Overture - Core

echo Making overutre


echo making core

cd core
cmd.exe /Q /C mvn install -Dmaven.test.skip=true -o

cd..

TITLE Making Overture - Eclipse Projects
echo Making Eclipse:eclipse

cmd.exe /Q /C mvn eclipse:eclipse -o

TITLE Making Overture - Updating Generated plugins
echo Updating generated core components for the IDE

cd ide\generated
cmd.exe /Q /C mvn psteclipse:eclipse-plugin -o
cd..
cd..

TITLE Making Overture - Ready for Eclise import as Maven Projects
echo Now you can import (Maven Projects) the IDE module in eclipse and develop new stuff here. Remember to update classpath on the manifest in org.overture.ide.generated.* packages

goto END



:CLEAN
echo clean
TITLE Clean Overture - Core

echo Making overutre


echo clean core

cd core
cmd.exe /Q /C mvn clean -o

cd..

TITLE Clean Overture - Eclipse Projects
echo Making Eclipse:clean

cmd.exe /Q /C mvn eclipse:eclean -o


:END