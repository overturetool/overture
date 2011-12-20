Build overview

To build the IDE one of the build scripts must be modified to the local build environment:

* build.bat
* build.sh

The scripts must be changed according to the properties:

* ECLIPSE_HOME: The Eclipse installation to use for building
* BUILD_XML_PATH: Path to the checked out source build file e.g.
	"C:\overture\overturesvn\ide\build\"
* equinoxLauncherPluginVersion: The launcher in the ECLIPSE_HOME e.g.
	"1.0.201.R35x_v20090715"
* pdeBuildPluginVersion: The version of the PDE builder in the ECLIPSE_HOME/plugins e.g.
	"3.5.2.R35x_20100114"
* BASEOS=win32
* BASEWS=win32
* BASEARCH=x86


To setup the environment the following have to be done:

* Download the Eclipse base: http://download.eclipse.org/eclipse/downloads/
	http://download.eclipse.org/eclipse/downloads/drops/R-3.5.2-201002111343/index.php
* Download and integrate the Delta-pack:
	http://download.eclipse.org/eclipse/downloads/drops/R-3.5.2-201002111343/download.php?dropFile=eclipse-3.5.2-delta-pack.zip
* If needed download the draw2d jar as well.


Building and releasing:

To make a build the customized script should be moved to a folder of choice (BUILD_DIR):
e.g.: c:\overtureIdeBuild.
* Change the property platformReleaseVersion to the new release version in the build.properties or set is in the script by -DplatformReleaseVersion=0.3.x
* Run the build script. 
	After completion a BUILD_DIR/build/I.OvertureIde will exists with the new IDE builds and
	A folder BUILD_DIR/build/buildRepo will exists which contains the repository.
* Releasing the IDE:
	Upload the OvertureIde*.zip to sf.net by:	
		 rsync -e ssh %FILE% lausdahl,overture@frs.sourceforge.net:%DEST%
		 Where:
		 	%FILE% is the zip file and
		 	%DEST% is the path at sf:
		 		/home/pfs/project/o/ov/overture/Overture_IDE/0.3.x/  
	Upload the new repository for self-update (only supported from 0.3.x)
		FTP: 
			Server=web.sourceforge.net
			Port=21
			User (USER is your sf user name)=USER,overture
			Password="Your sf password"
			Remote dir=/home/groups/o/ov/overture/htdocs/ide/repository
