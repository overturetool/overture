This VDM++ model is made by August Ribeiro as input for the VDM
courses delivered at IHA in Denmark. It is a concurrent version 
of the Minimum Safety Altitude Warning System (MSAW) example.

2011-12-28 This VDM++ model has been updated by Rasmus Lauritsen 
with the addition of a swing java radar display. The Radar.vdmpp 
model is now hooked up the with Radar display. The radar display 
will make a 360 degrees scan everytime the "Scan" operation on 
the Radar is invoked.

lib/radar.jar contains binary and source code for the java radar 
display.

#******************************************************
#  AUTOMATED TEST SETTINGS
#------------------------------------------------------
#AUTHOR= Augusto Ribeiro
#LIB= IO; MATH
#LANGUAGE_VERSION=classic
#INV_CHECKS=true
#POST_CHECKS=true
#PRE_CHECKS=true
#DYNAMIC_TYPE_CHECKS=true
#SUPPRESS_WARNINGS=false
#ENTRY_POINT=new World().Run()
#EXPECTED_RESULT=NO_ERROR_INTERPRETER
#******************************************************
