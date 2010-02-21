This example due to Abrial has been translated from the 
B-notation into VDM-SL. It demonstrates how an event-based 
system may be modeled using the specification language 
of the Vienna Development Method. In the following, 
operations specify the events which can be initiated 
either by the system or by a subscriber (user). An 
implicit style using pre- and post-conditions has 
been chosen, in order to model the system's state 
transitions. The model of the telephone exchange is 
centred around a set of subscribers who may be engaged 
in telephone conversations through a network controlled 
by an exchange. 

#******************************************************
#  AUTOMATED TEST SETTINGS
#------------------------------------------------------
#AUTHOR= Bernhard Aichernig
#LANGUAGE_VERSION=classic
#INV_CHECKS=true
#POST_CHECKS=true
#PRE_CHECKS=true
#DYNAMIC_TYPE_CHECKS=true
#SUPPRESS_WARNINGS=false
#DOCUMEN= telephone.tex
#ENTRY_POINT=
#EXPECTED_RESULT=NO_ERROR_TYPE_CHECK
#******************************************************