The tracker example is used in the mapping chapter of the VDM-SL
book by John Fitzgerald and Peter Gorm Larsen to introduce 
mappings and mapping operators concerns a system for tracking the 
movement of containers of hazardous material between phases of 
processing in a nuclear reprocessing plant. It is inspired by 
the formal model of a plant controller architecture developed 
by Manchester Informatics Ltd. in collaboration with British 
Nuclear Fuels (Engineering) Ltd. (BNFL) in 1995. More can be 
read about this in:

J.S. Fitzgerald and C.B. Jones, Proof in VDM: Case Studies, 
Chapter: Proof in the Validation of a Formal Model of a 
Tracking System for a Nuclear Plant, Springer-Verlag,
FACIT Series, 1998.
#******************************************************
#  AUTOMATED TEST SETTINGS
#------------------------------------------------------
#LANGUAGE_VERSION=classic
#AUTHOR= John Fitzgerald and Peter Gorm Larsen
#INV_CHECKS=true
#POST_CHECKS=true
#PRE_CHECKS=true
#DYNAMIC_TYPE_CHECKS=true
#SUPPRESS_WARNINGS=false
#ENTRY_POINT=DEFAULT`Permission(tracker_inital,cid1,mk_token("Unpacking"))
#EXPECTED_RESULT=NO_ERROR_TYPE_CHECK
#******************************************************
