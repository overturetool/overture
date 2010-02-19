This example was produced by Nick Battle and it is used in the VDMJ user
manual to illustrate different features of VDMJ. It models the behaviour
of the 32-bit shared memory quadrants of HP-UX, using a record type M to 
represent a block of memory which is either <FREE> or <USED>, and a 
sequence of M records to represent a Quadrant.

The specification output indicates which allocation policy, first-fit or 
best-fit (or neither), produces the most memory fragmentation.

#******************************************************
#  AUTOMATED TEST SETTINGS
#------------------------------------------------------
#AUTHOR= Nick Battle
#LANGUAGE_VERSION=classic
#INV_CHECKS=true
#POST_CHECKS=true
#PRE_CHECKS=true
#DYNAMIC_TYPE_CHECKS=true
#SUPPRESS_WARNINGS=false
#ENTRY_POINT= M`main(5,100)
#EXPECTED_RESULT=NO_ERROR_TYPE_CHECK
#******************************************************