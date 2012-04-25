#!/bin/bash
echo Parsing $1

java -cp "simpleParser.jar:vdmj-2.0.1-jar-with-dependencies.jar" Simple.ASTParseToFile -vdmpp $1
