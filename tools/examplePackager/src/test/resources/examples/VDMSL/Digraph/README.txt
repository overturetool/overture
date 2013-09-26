
The specification describes how directed graphs and relations over
such graphs can can tested for relevant properties and manipulated in
different ways. This specification is produced by Janusz Laski from
Oakland University in the USA. Most of the definitions in this
specification can be interpreted.

This model is only an illustration of the problems germane to automatic 
software analysis. To get a better understanding of the scope of the 
analysis consult the text "Software Verification and Analysis, An 
Integrated, Hands-on -- Approach," by Janusz Laski w/William Stanley, 
Springer 2009. A brief online introduction is offered on the Website
www.stadtools.com.
#******************************************************
#  AUTOMATED TEST SETTINGS
#------------------------------------------------------
#AUTHOR= Janusz Laski
#LANGUAGE_VERSION=classic
#INV_CHECKS=true
#POST_CHECKS=true
#PRE_CHECKS=true
#DYNAMIC_TYPE_CHECKS=true
#SUPPRESS_WARNINGS=false
#ENTRY_POINT=relations`IsTransitive(relations`A5)
#ENTRY_POINT=relations`IsTransitive(A7)
#EXPECTED_RESULT=NO_ERROR_INTERPRETER
#******************************************************
