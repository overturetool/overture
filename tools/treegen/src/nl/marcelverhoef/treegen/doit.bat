..\..\..\..\resource\byaccj -d -Jclass=TreeParser -Jpackage=nl.marcelverhoef.treegen -Jthrows=java.io.IOException -Jnorun TreeGen.y
java -classpath ..\..\..\..\resource\JFlex.jar JFlex.Main -nobak -q TreeGen.l
