The treegen tool is used to generate Java code to manage abstract syntax trees.
The sources in this directory enable you to build the treegen tool.
It should compile as-is, just run 'maven install' in this directory.
The resulting jar file can then be found in the 'target' directory.

REGENERATING THE SCANNER/PARSER (MANUAL TASK!!)

The treegen tool is developed using the jflex and byaccj tools.
The lex and yacc files are provided as well as the generated java code.
However, binary versions of jflex and byaccj (for Windows) are provided in the resource directory.
So, if you change either

src\main\java\org\overture\tools\treegen\TreeGen.l

or

src\main\java\org\overture\tools\treegen\TreeGen.y

then run (manually):

src\main\java\org\overture\tools\treegen\doit.bat

This will regenerate the scanner and parser Java sources files.
Then rerun 'mvn install' in order to recompile the treegen tool.

BOOTSTRAPPING THE AST USED BY TREEGEN

First compile the treegen tool as-is.
Then go to the 'target' directory.
Then execute 'java -jar treegen-x.y.z.jar ..\resource\treegen.ast'
This will regenerate the Java AST code used by treegen in ..\src\main\java
Then run 'mvn install' again to recompile treegen, which will include the bootstrapped AST code

Marcel Verhoef
05/08/2010
