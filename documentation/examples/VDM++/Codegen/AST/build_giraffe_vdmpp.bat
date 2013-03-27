@echo off
echo "Building AST for Giraffe"


echo Building VDM-PP...
echo    Generating Interfaces
java -jar astgen-2.0.0-jar-with-dependencies.jar -lang vdm -kind intf -out . -class Giraffe giraffe.ast

echo    Generating Implementation
java -jar astgen-2.0.0-jar-with-dependencies.jar -lang vdm -kind impl -out . -class Giraffe giraffe.ast

