cmd.exe /C runReduction.bat Test vdmrt "CarRadioNavi"

cmd.exe /C runReductionVdm10.bat DEFAULT vdmsl "AlarmSL"
cmd.exe /C runReductionVdm10.bat DEFAULT vdmsl "Dwarf"
cmd.exe /C runReductionVdm10.bat DEFAULT vdmsl "cashdispenser"

cmd.exe /C runReduction.bat System vdmpp "CashDispenserPP"

cmd.exe /C runReduction.bat Test1 vdmpp "alarmtrace"
cmd.exe /C runReduction.bat UseBuffers vdmpp "buffers"

cmd.exe /C runReduction.bat UseFileSystemLayerAlg vdmpp "VFS"

cmd.exe /C runReduction.bat UseKLV vdmpp "KLV"

cmd.exe /C runReduction.bat UseATC vdmpp "MSAWseqPP"

cmd.exe /C runReduction.bat Test vdmpp "SAFERPP"

cmd.exe /C runReduction.bat UseStack vdmpp "stackPP"
cmd.exe /C runReduction.bat UseTree vdmpp "treePP"


cmd.exe /C runReduction.bat UseGP vdmpp "worldcupPP"

cmd.exe /C runReduction.bat POP3Test vdmpp "pop3"

cmd.exe /C runReduction.bat TestTraces vdmpp "TrayControl"

