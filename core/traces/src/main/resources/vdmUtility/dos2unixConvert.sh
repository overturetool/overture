#!/bin/sh
#
#Remove windows control char from text files
#
#To install dos2unix use this line
# sudo apt-get install tofrodos
#
#Then run the command below to convert this file and then execute it to convert the rest of the files.
# dos2unix dos2unixConvert.sh 
# ./dos2unixConvert.sh


dos2unix UpdateTestSuite.sh
dos2unix UpdateVDMJmodel.sh
dos2unix UpdateVDMToolsCmd.sh
dos2unix ../VDMToolsCmdModel/run.sh
dos2unix ../VDMJmodel/vdmArgumentLinux.sh
chmod +x *.sh
