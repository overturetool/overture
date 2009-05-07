TITLE Updating TestCase
echo off
cls
echo Updating
java -classpath "lib\StdLib-1.0.0.jar;lib\parser-1.0.0.jar;overtureUtility.jar;lib\umltrans-1.0.0.jar;lib\ast-1.0.0.jar" MainClass -vdmCmdEx -project "..\..\java\CT_vdmtools_project.prj" -outputDir "vdmt"