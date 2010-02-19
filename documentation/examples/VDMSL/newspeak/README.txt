The programming language NewSpeak? is a language designed 
specifically for use in safety-critical systems. It employs 
the notion of Orwellian programming - undesirable properties 
are avoided by restricting the syntax of the programming 
language. This is a formal semantics for the language in 
VDM-SL. Details of the language and its semantics: 

P. Mukherjee, "A Semantics for NewSpeak? in VDM-SL". In 
T. Denvir, M. Naftalin, M. Bertran (eds), "FME '94: 
Industrial Benefit of Formal Methods", Springer-Verlag, 
October 1994.
 
I.F. Currie, "NewSpeak - a reliable programming language". 
In C. Sennett (ed), "High-Integrity Software", Pitman 1989. 
#******************************************************
#  AUTOMATED TEST SETTINGS
#------------------------------------------------------
#AUTHOR= Paul Mukherjee
#LANGUAGE_VERSION=classic
#INV_CHECKS=true
#POST_CHECKS=true
#PRE_CHECKS=true
#DYNAMIC_TYPE_CHECKS=true
#SUPPRESS_WARNINGS=false
#ENTRY_POINT=DEFAULT`max({3,1,5,8,3,2,5,4,22})
#EXPECTED_RESULT=NO_ERROR_TYPE_CHECK
#******************************************************