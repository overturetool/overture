..\..\..\..\..\..\..\resource\byaccj -d -Jclass=TreeParser -Jpackage=org.overture.tools.treegen -Jthrows=java.io.IOException -Jnorun TreeGen.y
java -classpath ..\..\..\..\..\..\..\resource\JFlex.jar JFlex.Main -nobak -q TreeGen.l
