The Non-programmer database system (NDB) is a nicely engineered binary 
relational database system invented by Norman Winterbottom of IBM. The 
formal Specification of NDB was originally undertaken by Anne Walshe, 
who has subsequently documented the specification and its refinement. 
NDB has been used as an example problem for modular specification in 
VDM-SL. However, the version available here is a "flat" specification. 
The postscript file includes a significant description of the validation 
of the specification using execution. Test coverage is not used though. 
Relevant publications are: 

A. Walshe, "NDB: The Formal Specification and Rigorous Design of a 
Single-User Database System", in C.B. Jones and R.C. Shaw (eds), 
"Case Studies in Systematic Software Development", Prentice Hall 
1990, ISBN 0-13-116088-5 

J.S. Fitzgerald and C.B. Jones, "Modularizing the Formal Description 
of a Database System", in D. Bjorner, C.A.R. Hoare and H. Langmaack 
(eds), VDM '90: VDM and Z - Formal Methods in Software Development, 
Springer-Verlag, LNCS 428, 1990 
#******************************************************
#  AUTOMATED TEST SETTINGS
#------------------------------------------------------
#AUTHOR= Rich Bradford
#LANGUAGE_VERSION=classic
#INV_CHECKS=true
#POST_CHECKS=true
#PRE_CHECKS=true
#DYNAMIC_TYPE_CHECKS=true
#SUPPRESS_WARNINGS=false
#ENTRY_POINT=
#EXPECTED_RESULT=NO_ERROR_TYPE_CHECK
#******************************************************