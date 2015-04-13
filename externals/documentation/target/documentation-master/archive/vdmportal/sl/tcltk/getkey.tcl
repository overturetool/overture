#----------------------------------------------------------------------------
#  getkey.tcl
#
#  tcl-script for defining key value for lookup in db
#  is evaluated in function GetKey in file gui_func.cc
#  version: 2.0
#  bf                                                             20.02.1996
#----------------------------------------------------------------------------
# global variables

# variable linked with c variable
set key ""

# variable local used for input
set keyval ""

set Done 0

proc take {} {
global keyval

wm geometry . +200+150
wm title . {DB EXAMPLE}
frame .top -relief raised -bd 1

frame .top.f1
label .top.f1.bl1 -text {Key: }
entry .top.f1.key -width 20 -textvariable keyval -relief sunken -bd 2

frame .top.f2
button .top.f2.ok -text OK -command takebook
button .top.f2.cancel -text cancel -command {exit }

pack .top -side top -fill x
pack .top.f1 .top.f2 -side top -fill x
pack .top.f1.bl1 .top.f1.key -side left -padx 2m -pady 2m
pack .top.f2.ok .top.f2.cancel -side left -expand 1 -padx 2m -pady 2m
bind .top.f1.key <Return> GetKey
grab .
focus .top.f1.key
} 


proc GetKey {} {
  global keyval
  set keyval [.top.f1.key get]
  focus .top.f2.ok
}


# Globale Variable setzen, die die Werte uebernimmt 
proc takebook {} {
   global key
   global keyval
    if { ([string length $keyval] == 0)} {
      tk_dialog .msg {Error Message} {No key value is specified!}\
		{} -1 {Ok}
      focus .top.f1.key
    } else {
      if { [regexp {^[0-9]+$} $keyval] == 0} {
        tk_dialog .msg {Error Message} {Key has to be a numeric value!} \
	      {} -1 {Ok}
        set keyval ""
        focus .top.f1.key
  } else {
     set key $keyval;  destroy .
     global Done
     set Done 1
}   }
}   

take
