This was the first model Marcel Verhoef tried to make of the car
radio navigation example using the original version of VICE with
one CPU. This failed and as a consequence Marcel Verhoef and Peter
Gorm Larsen came up with an improved version of VDM-RT with 
multiple CPUs connected with BUSses.

#******************************************************
#  AUTOMATED TEST SETTINGS
#------------------------------------------------------
#AUTHOR= Marcel Verhoef
#LIB= IO
#LANGUAGE_VERSION=classic
#INV_CHECKS=true
#POST_CHECKS=true
#PRE_CHECKS=true
#DYNAMIC_TYPE_CHECKS=true
#SUPPRESS_WARNINGS=false
#ENTRY_POINT=new RadNavSys(1).Run()
#ENTRY_POINT=new RadNavSys(2).Run()
#EXPECTED_RESULT=NO_ERROR_TYPE_CHECK
#******************************************************