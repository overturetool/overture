#!/bin/sh
export VDMTOOLSPATH=/Applications/vdmpp/bin
echo $VDMTOOLSPATH
export RTINFO=rtinfo.ast
export ARGUMENT=argumentFile.arg
echo "Copying rtinfo..."
cp rtinfo.ast ../main/vpp/
echo "Copying argument file..."
cp argumentFile.arg ../main/vpp/
echo "Running script..."
sh runVDM.sh

