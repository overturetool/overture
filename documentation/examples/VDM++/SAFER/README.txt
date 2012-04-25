This specification is a VDM++ model of the SAFER 
(Simplified Aid for EVA Rescue) example presented in 
the second volume of the NASA guidebook on formal 
methods. 

Here Appendix C contains a complete listing of the 
SAFER system using PVS. We have translated this PVS 
specification rather directly into VDM-SL previously 
and here that model is again moved to VDM++. In the 
VDM++ model we have abstracted away form a number of 
parts which has been left as uninterpreted functions 
in the PVS model. This has been done because we have 
defined the purpose of the model to clarify the 
functionality of the thruster selection logic and the 
protocol for the automatic attitude hold functionality. 
Otherwise we have on purpose varied as little as 
possible from the given PVS model. In order to 
visualise this example the dynamic link feature is 
illustrated as well. In the test class Test there are 
a few examples of using the traces primitives used for 
test automation. 

More explanation about this work can be found in the papers: 
 *Sten Agerholm and Peter Gorm Larsen, Modeling and 
  Validating SAFER in VDM-SL, ed: Michael Holloway, 
  in "Fourth NASA Langley Formal Methods Workshop", 
  NASA, September 1997. 
 *Sten Agerholm and Wendy Schafer, Analyzing SAFER using 
  UML and VDM++, ed: John Fitzgerald and Peter Gorm Larsen, 
  in "VDM Workshop at the Formal Methods 1999 conference, 
  Toulouse 

#******************************************************
#  AUTOMATED TEST SETTINGS
#------------------------------------------------------
#AUTHOR= Sten Agerholm and Peter Gorm Larsen
#LANGUAGE_VERSION=classic
#INV_CHECKS=true
#POST_CHECKS=true
#PRE_CHECKS=true
#DYNAMIC_TYPE_CHECKS=true
#SUPPRESS_WARNINGS=false
#ENTRY_POINT=new Test().BigTest()
#EXPECTED_RESULT=NO_ERROR_INTERPRETER
#******************************************************
