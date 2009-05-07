#!/bin/sh
export VDMTOOLSPATH="C:/Program Files/The VDM++ Toolbox v8.2b/bin"
export RTINFO_PATH="../VDMToolsCmdModel/rtinfo.ast"

cp $RTINFO_PATH vdm.tc
sh runVdmCoverage.sh
rm -rf vdm.tc