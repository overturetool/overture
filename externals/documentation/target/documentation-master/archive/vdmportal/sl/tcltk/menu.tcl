#----------------------------------------------------------------------------
#  menu.tcl
#
#  tcl-script for selecting a functions to execute
#  is evaluated in the function Menu in file gui_func.cc
#  Vers: 1.0
#  bf                                                             20.02.1996 
#----------------------------------------------------------------------------

set Done 0

proc menu {} {
  global choose
  set choose ""
  global Done 
 
  wm geometry . +200+100
  wm geometry . 230x350
  wm title . {DB EXAMPLE}
  
  frame .top -relief groove -bd 1
  frame .bot -relief groove -bd 1

  pack .top .bot -side top -fill both

  button .top.b1 -text Insert -command {set choose 1; destroy .; set Done 1 }
  button .top.b2 -text Defined -command {set choose 2; destroy .; set Done 1 }  
  button .top.b3 -text Lookup -command {set choose 3; destroy .; set Done 1 }  
  button .top.b4 -text Remove -command {set choose 4; destroy .; set Done 1 }
 
  button .bot.quit -text Quit -command {set choose 0; destroy .; set Done 1 }
  pack .top.b1 .top.b2 .top.b3 .top.b4 -side top -fill x \
-pady 5m -padx 5m

  pack .bot.quit -side top -pady 5m -pady 5m

}

menu