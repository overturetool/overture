#!/bin/sh
# Make of Overture

# install function
install()
{
echo "install"
echo
echo "Making Overture - Core"
echo
echo
echo "Making overutre"
echo
echo "making core"
cd core

mvn install -Dmaven.test.skip=true

cd ..
echo
echo Making Overture - Eclipse Projects
echo
echo Making Eclipse:eclipse
echo
mvn eclipse:eclipse

echo
echo "Making Overture - Updating Generated plugins"
echo
echo "Updating generated core components of the IDE"

cd ide/generated
mvn psteclipse:eclipse-plugin
cd ..
cd ..
echo
echo "Making Overture - Ready to Eclise import as Maven Projects"
echo
echo "Now you can import (Maven Projects) the IDE module in eclipse and develop new stuff here. Remember to update classpath on the manifest in org.overture.ide.generated.* packages";

}


#clean
clean()
{
echo
echo "Clean Overture - Core"
echo
echo "Making overutre"
echo
echo "clean core"

cd core
mvn clean -o

cd ..
echo
echo "Clean Overture - Eclipse Projects"
echo
echo "Making Eclipse:clean"

mvn eclipse:eclean -o


}

while [ ! -z "$1" ]; do
case $1 in
    install)
install
;;
clean)
clean
;;

esac
shift
done
