@echo off
echo "Building AST for Simple"


echo Building VDM-PP...
echo    Generating Interfaces
java -jar astgen-2.0.0-jar-with-dependencies.jar -lang vdm -kind intf -out . -class Simple simple.ast >log.txt

echo    Generating Implementation
java -jar astgen-2.0.0-jar-with-dependencies.jar -lang vdm -kind impl -out . -class Simple simple.ast >log.txt

