#----------------------------------------------------------------------------
#  showdata.tcl
#
#  tcl-script which display the result of an lookup function
#  is evaluated in function ShowData in file gui_func.cc
#  version: 2.0
#  bf                                                             20.02.1996
#----------------------------------------------------------------------------
# the c function defines and set the variables data and key

proc ShowStatus {} {
  global key; global data
  global Done
  set Done 0

  wm geometry . +200+120
  wm title . {DB Example }
  wm minsize . 200 160
  
  frame .top 
 
  message .top.msg1 -width 8c  -justify right \
  -font -Adobe-Times-Medium-R-Normal-*-180-* \
  -text "                           Key: $key"

  frame .top.f1 -relief groove -bd 2
  message .top.f1.msg1 \
  -font -Adobe-Times-Medium-R-Normal-*-180-*\
  -text {Data:}
  message .top.f1.msg2  -justify left -bd 1 \
  -font -Adobe-Times-Medium-R-Normal-*-180-*\
  -text $data
 
  frame .top.f2 -relief sunken -bd 1
  button .top.f2.ok -text ok -command {destroy .; set Done 1}
  
  pack .top -side top -fill x
  pack .top.msg1 .top.f1 .top.f2 -side top -fill x
  pack .top.f1.msg1 .top.f1.msg2 -side top -fill x
  pack .top.f2.ok -side left -expand 1 -padx 2m -pady 2m
}   

ShowStatus



