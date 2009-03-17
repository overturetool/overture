TITLE Updating TestCase
echo off
cls
echo Updating
java -classpath "lib\VDM.jar;lib\org.overturetool.parser.jar;overtureUtility.jar;lib\org.overturetool.umltrans-1.0.0.jar" MainClass -vdmCmdEx -project "c:\tracesSpec\src\overtureTraces\src\main\vpp\CT.prj" -outputDir ..\VDMToolsCmdModel