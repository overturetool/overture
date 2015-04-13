#----------------------------------------------------------------------------
#  showmsg.tcl
#
#  tcl-script which display an error message on the screen
#  is evaluated in function ShowMsg in file gui_func.cc
#  version: 2.0
#  bf                                                             20.02.1996
#----------------------------------------------------------------------------
# the c function defines and set the variables  err_msg

proc ShowMsg {} {
  global err_func
  global err_msg
  global Done
  set Done 0

  wm geometry . +300+200
  wm title . {Error Message}

  frame .top -relief raised -bd 1
  pack .top -side top -fill both
  frame .bot -relief raised -bd 1
  pack .bot -side bottom -fill both

  frame .f1
  pack .f1 -in .top -side right -padx 5m -pady 5m -fill both 
  message .msg1  -width 3i -text "Precondition violated in \"$err_func\"!"\
  -font -Adobe-Times-Medium-R-Normal-*-180-*
 
  message .msg2  -width 3i -text $err_msg\
  -font -Adobe-Times-Medium-R-Normal-*-180-*
 
  pack .msg1 .msg2 -in .f1 -side top -expand 1 -fill both 
  

  label .bitmap -bitmap warning
  pack .bitmap -in .top -side left -padx 5m -pady 5m

  button .button -text {Ok} -command {destroy .; set Done 1}
  pack .button -in .bot -side left -expand 1 -padx 3m -pady 3m
} 

ShowMsg 