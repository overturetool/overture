TITLE VDMJ model transformation from VDM Tools
echo off
cls

java -classpath "lib\StdLib-1.0.0.jar;lib\parser-1.0.0.jar;overtureUtility.jar;lib\umltrans-1.0.0.jar;lib\ast-1.0.0.jar" MainClass -vdmjEx -vdmjDir "vdmj" -project "..\..\java\CT_vdmtools_project.prj" -vdmjSpec "vdmj" -argumentFile "vdmj\vdmArgument.bat"