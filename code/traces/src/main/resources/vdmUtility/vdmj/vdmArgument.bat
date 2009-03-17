cls
java -classpath "..\lib\StdLib-1.0.0.jar;..\lib\parser-1.0.0.jar;..\lib\vdmj-1.0.0.jar;..\lib\ast-1.0.0.jar" -Xmx512m org.overturetool.vdmj.VDMJ -vdmpp -w -q -t UTF8 -e "new SpecTestSuite().Execute()" FILES > vdmj.log
type vdmj.log

