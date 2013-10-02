This example is written by Paul Mukherjee and it is used in the VDM++ book
John Fitzgerald, Peter Gorm Larsen, Paul Mukherjee, Nico Plat and Marcel 
Verhoef. Validated Designs for Object-oriented Systems, Springer, New York. 
2005, ISBN 1-85233-881-4. The concurrent system in question is a server 
for the POP3 protocol. This is a protocol supported by all major email c
lients to fetch email messages from the email server. 
#******************************************************
#  AUTOMATED TEST SETTINGS
#------------------------------------------------------
#AUTHOR= Paul Mukherjee
#LANGUAGE_VERSION=classic
#INV_CHECKS=true
#LIB=IO
#POST_CHECKS=true
#PRE_CHECKS=true
#DYNAMIC_TYPE_CHECKS=true
#SUPPRESS_WARNINGS=false
#ENTRY_POINT=new POP3Test().Test1()
#ENTRY_POINT=new POP3Test().Test2()
#EXPECTED_RESULT=NO_ERROR_INTERPRETER
#******************************************************
