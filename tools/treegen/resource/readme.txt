This directory contains some resources required to *bootstrap* treegen.
It contains precompiled  binaries of the well-known BYACCJ and JFLEX.
Neither the normal build process nor the resulting treegen tool depends on these!
They are only required for maintenaince of treegen itself.

o byaccj.exe is [a Windows executable] needed to regenerate the TreeParser classes.
o JFlex.jar is [a Java archive] needed to regenerate the TreeScanner classes.
o treegen.ast is the file that describes the abstract syntax elements used by treegen.

Please read ..\readme.txt first!
