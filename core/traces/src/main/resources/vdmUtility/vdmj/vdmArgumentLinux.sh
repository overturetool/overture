#!/bin/sh
#cp vdmjSpecificModel/*.vpp vpp
java -jar -Xmx512m vdmj.jar -vdmpp -w -q -t UTF8 -e "new SpecTestSuite().Execute()" FILES

