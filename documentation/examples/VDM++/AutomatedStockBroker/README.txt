The system is an automated stock broker, where you can specify a list
of stocks which automaticly, can either be bought or sold. This is
done by defining a prioritised list of stocks to observe, which each
has defined a trigger that tells in which situation the system should
react with either a buy or a sell action. The trigger is a rule
defined upon the history and the current value of the stock. This
model is made by Anders Kaels Malmos as a small mini-project in a
course on "Modelling of Mission Critical Systems" (see
https://services.brics.dk/java/courseadmin/TOMoMi/pages/Modelling+of+Mission+Critical+Systems). 

More information about the model and the purpose of it can be found in
the ProjectReport.pdf file included in the zip file with the source files.

#******************************************************
#  AUTOMATED TEST SETTINGS
#------------------------------------------------------
#AUTHOR= Anders Kaels Malmos
#LIB=IO;MATH
#LANGUAGE_VERSION=classic
#INV_CHECKS=true
#POST_CHECKS=true
#PRE_CHECKS=true
#DYNAMIC_TYPE_CHECKS=true
#SUPPRESS_WARNINGS=false
#ENTRY_POINT=new World().Run()
#EXPECTED_RESULT=NO_ERROR_INTERPRETER
#******************************************************
