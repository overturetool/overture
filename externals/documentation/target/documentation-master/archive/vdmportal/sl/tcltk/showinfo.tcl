#----------------------------------------------------------------------------
#  showinfos.tcl
#
#  tcl-script which shows a status information at the beginning
#  After pressing th OK  button the status information is not visisble
#  anymore
#  is evaluated in the function ShowStatus in file gui_func.cc
#  Vers: 1.0
#  bf                                                             20.02.1996 
#----------------------------------------------------------------------------


proc showinfo {} {

  global Done
  set done 0

  wm geometry . +150+150
   wm title . {DB Example}

  frame .top -relief groove -bd 1
  frame .bot -relief groove -bd 1
  pack .top .bot -side top -fill both

  message .top.msg1 -width 9c \
  -font -Adobe-Times-Medium-R-Normal-*-180-*\
  -text "This example shows the combination of a VDM-SL specification \
  of a database and of an user interface implemented in C++ and tcl-tk \
  in the IFAD VDM-SL Toolbox.  \n\n\
  Enjoy this new way of prototyping!"

  button .top.ok -text OK -command {destroy .; set Done 1}

  message .bot.msg1 -width 8c -text\
 " IFAD                                                  TU Graz\n\
Brigitte Froehlich                       February 1996"

  pack .top.msg1 .top.ok -side top  -padx 5m -pady 5m
  pack .bot.msg1 -side bottom -fill x
}

showinfo