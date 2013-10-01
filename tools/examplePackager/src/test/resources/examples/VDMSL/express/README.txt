The (building) industry in Europe currently uses the 
ISO-STEP standard to define information models with the 
aim to exchange data about those information models. The 
ISO-STEP standard contains the EXPRESS modelling language 
and several programming language bindings and an ASCII 
neutral format to implement interfaces to those models. 
Unfortunately, industry has not reached consensus on a 
particular information model, therefore multiple models 
exist. This raises the need to migrate instances from one 
model to another and vice-versa, commonly referred to as 
the "mapping". The aim of this exercise was to determine 
the applicability of VDM-SL with respect to these types 
of problems. For more details on the mapping issue down-
loadable copies of two papers resulting from this research 
is available. The example shows a simple VDM-SL abstract 
syntax representation of the ISO STEP part 21 physical 
file format and a transformation process for a particular 
set of abstract syntax instances. It implements a mapping 
between the relational model representation (rmrep) into 
a simple polynomial representation. 

#******************************************************
#  AUTOMATED TEST SETTINGS
#------------------------------------------------------
#AUTHOR= Marcel Verhoef
#LANGUAGE_VERSION=classic
#INV_CHECKS=true
#POST_CHECKS=true
#PRE_CHECKS=true
#DYNAMIC_TYPE_CHECKS=true
#SUPPRESS_WARNINGS=false
#ENTRY_POINT=Database`Transform()
#EXPECTED_RESULT=NO_ERROR_INTERPRETER
#******************************************************
