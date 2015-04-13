This is a Specification of the File System Layer, sliced at the 
FS_DeleteFileDir operation, as defined in the INTEL Flash File 
System document. It includes: a VDM++ model that can be model 
checked in an equivalent Alloy model; and an adapted version 
of the VDM++ model to be used in the Overture Automated Proof 
Support system. In the test class UseFileSystemLayerAlg there 
are a few examples of using the traces primitives used for 
test automation. This model has been developed by Miguel 
Ferreira
#******************************************************
#  AUTOMATED TEST SETTINGS
#------------------------------------------------------
#AUTHOR= Miguel Ferreira
#LANGUAGE_VERSION=classic
#INV_CHECKS=true
#POST_CHECKS=true
#PRE_CHECKS=true
#DYNAMIC_TYPE_CHECKS=true
#SUPPRESS_WARNINGS=false
#ENTRY_POINT=new UseFileSystemLayerAlg().dummy()
#EXPECTED_RESULT=NO_ERROR_INTERPRETER
#******************************************************
