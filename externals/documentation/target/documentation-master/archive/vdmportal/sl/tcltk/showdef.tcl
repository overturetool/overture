#----------------------------------------------------------------------------
#  showdef.tcl
#
#  tcl-script which display a key and a message either key is defined in
#  database or not.
#  is evaluated in function ShowDefined in file gui_func.cc
#  version: 1.0
#  bf                                                             20.02.1996
#----------------------------------------------------------------------------
# the c function defines and set the variables key and flag

set Done 0 

proc CreateWd {message} {

  wm geometry . +200+120

  frame .top -relief raised -bd 1
  pack .top -side top -fill both
  frame .bot -relief raised -bd 1
  pack .bot -side bottom -fill both

  message .msg  -width 3i -text $message\
  -font -Adobe-Times-Medium-R-Normal-*-180-*
  pack .msg -in .top -side right -expand 1 -fill both \
   -padx 5m -pady 5m
  label .bitmap -bitmap warning
  pack .bitmap -in .top -side left -padx 5m -pady 5m

  button .button -text {Ok} -command {destroy .; set Done 1}
  pack .button -in .bot -side left -expand 1 -padx 3m -pady 3m
}   

proc ShowDef {} {
  global key
  global flag

    if {$flag == 1} {
        set msgtxt "Key \"$key\" is defined in Database!\n"
	CreateWd $msgtxt
    } else {
      set msgtxt "Key \"$key\" is not defined in Database!\n"
      CreateWd $msgtxt
}   }
ShowDef
