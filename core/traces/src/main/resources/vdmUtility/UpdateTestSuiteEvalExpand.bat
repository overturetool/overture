TITLE Updateing TestCase
echo off
cls
echo Updating
java -classpath "lib\StdLib-1.0.0.jar;lib\parser-1.0.0.jar;overtureUtility.jar;lib\umltrans-1.0.0.jar;lib\ast-1.0.0.jar" MainClass -gTC -dir "..\..\..\test\resources\testData" -name TC -outputDir ..\..\vpp\test -specTestHelpClass SpecTest