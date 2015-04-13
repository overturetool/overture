#----------------------------------------------------------------------------
#  getdata.tcl
#
#  tcl-script for defining key value and data value for the insert in db
#  is evaluated in the function GetData in file gui_func.cc
#  Vers: 2.0
#  bf                                                             20.02.1996 
#----------------------------------------------------------------------------
# global variables

# Variables linked with c variables 
set key ""             
set data ""
# variables local used for input
set keyval ""
set datval ""


proc take {} {
global keyval; global datval

global Done
set Done 0

wm geometry . +200+120
wm title . {DB EXAMPLE}
frame .top -relief raised -bd 1

frame .top.f1 -bd 2
label .top.f1.bl1 -text {Key:  }
entry .top.f1.key -width 20 -textvariable keyval -relief sunken -bd 2

frame .top.f2
label .top.f2.bl2  -text {Data: }
entry .top.f2.data -width 20 -textvariable datval -relief sunken -bd 2

frame .bot -relief raised -bd 1
button .bot.ok -text OK -command {takebook}
button .bot.cancel -text CANCEL -command {exit }

pack .top -side top -fill x
pack .top.f1 .top.f2 -side top -fill both 
pack .top.f1.bl1 .top.f1.key -side left -fill x -padx 2m -pady 2m
pack .top.f2.bl2 .top.f2.data -side left -fill x -padx 2m -pady 2m
pack .bot -side top -fill x
pack .bot.ok .bot.cancel -side left -expand 1 -padx 2m -pady 2m
bind .top.f1.key <Return> GetKey
bind .top.f2.data <Return> GetData
grab .
focus .top.f1.key
} 


proc GetKey {} {
  global keyval
  set keyval [.top.f1.key get]
  focus .top.f2.data
}  

proc GetData {} {
  global datval
  set datval [.top.f2.data get]
  focus .bot.ok
}

# Globale Variable setzen, die die Werte uebernimmt 
proc takebook {} {
   global keyval; global datval
   global key; global data

    if { ([string length $keyval] == 0)} {
	tk_dialog .msg {Error Message} {No key value is specified!}\
		{} -1 {Ok}
             focus .top.f1.key
    } else {
        if { [regexp {^[0-9]+$} $keyval] == 0} {
           tk_dialog .msg {Error Message} {Key must be a numeric value!} \
	      {} -1 {Ok}
            set keyval ""
            focus .top.f1.key
         } else {
            if { [string length $datval] == 0} {
        	tk_dialog .msg {Error Message} {No data value is specified!}\
		{} -1 {Ok}
               focus .top.f2.data
            } else {
              set key $keyval; set data $datval; destroy .; 
	      global Done; set Done 1
    }   }   }
}   

take
