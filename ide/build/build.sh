BUILD_DIR=${PWD}
BUILD_DIR=$BUILD_DIR/build

echo Building overture
#/home/kela/Desktop/testBuildDir

ECLIPSE_HOME=/home/kela/Desktop/eclipse352
BASE=$ECLIPSE_HOME
BUILD_XML_PATH=/home/kela/overture/ide/build/
equinoxLauncherPluginVersion='1.0.201.R35x_v20090715'
pdeBuildPluginVersion='3.5.2.R35x_20100114'
BASEOS=linux32
BASEWS=gtk
BASEARCH=x86

echo Changing to build.xml directory

cd $BUILD_XML_PATH

LAUNCHER_PLUGIN=$ECLIPSE_HOME/plugins/org.eclipse.equinox.launcher_$equinoxLauncherPluginVersion.jar

echo Starting Eclipse ANT runner

java -cp $LAUNCHER_PLUGIN org.eclipse.equinox.launcher.Main -application org.eclipse.ant.core.antRunner -buildfile build.xml -Dbase=$BASE -DeclipseLocation=$ECLIPSE_HOME -DequinoxLauncherPluginVersion=$equinoxLauncherPluginVersion -DbuildDirectory=$BUILD_DIR -DpdeBuildPluginVersion=$pdeBuildPluginVersion -Dbaseos=$BASEOS -Dbasews=$BASEWS -Dbasearch=$BASEARCH

cd $BUILD_DIR

cd ..

echo Done




