TITLE CMD PP of VDM Tools
cls
SET VDMTOOLSPATH="C:\Program Files\The VDM++ Toolbox v8.2b\bin"
SET RTINFO_PATH="..\VDMToolsCmdModel\vdm.ast"
echo Execute
copy %RTINFO_PATH% vdm.tc
runVdmCoverage.bat
del vdm.tc
