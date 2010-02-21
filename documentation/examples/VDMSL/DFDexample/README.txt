
This specification describes how one can automatically transform Data
Flow Diagrams (DFD) into VDM-SL definitions. It is written as a flat
VDM-SL model in a purely executable style. However, in order to test
it at the top level one needs to construct a large test structure
which essentially is an AST for a DFD. This have been done in the past
but unfortunately the sources for this have been lost. This model was
basis for a paper published as:

"A Formal Semantics of Data Flows Diagrams", P.G.Larsen, N.Plat, 
H.Toetenel, Formal Aspects of Computing'' 1994, Vol 6, December


#******************************************************
#  AUTOMATED TEST SETTINGS
#------------------------------------------------------
#AUTHOR= Peter Gorm Larsen
#DOCUMENT= dfdexample.tex
#LANGUAGE_VERSION=classic
#INV_CHECKS=true
#POST_CHECKS=true
#PRE_CHECKS=true
#DYNAMIC_TYPE_CHECKS=true
#SUPPRESS_WARNINGS=false
#ENTRY_POINT=
#EXPECTED_RESULT=NO_ERROR_TYPE_CHECK
#******************************************************