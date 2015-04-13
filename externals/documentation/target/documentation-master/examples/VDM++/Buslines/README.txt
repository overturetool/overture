This example models bus lines in a city, in which passengers are to be 
transferred from stop to stop. Passengers with specific destinations 
will arrive at a central station, and the route and flow of the buses 
need to be planned to service the passenger in the best possible way. 
The number and routes of buses as wells as the inflow of passengers are 
variables. 
 
 Remote Debugger must be set to remote class:
	gui.BuslinesRemote
 
#******************************************************
#  AUTOMATED TEST SETTINGS
#------------------------------------------------------
#AUTHOR= Claus Ballegaard Nielsen
#LIB=IO,VDMUtil,MATH
#LANGUAGE_VERSION=classic
#INV_CHECKS=true
#POST_CHECKS=true
#PRE_CHECKS=true
#DYNAMIC_TYPE_CHECKS=true
#SUPPRESS_WARNINGS=false
#ENTRY_POINT= new World().Run()
#EXPECTED_RESULT=NO_ERROR_TYPE_CHECK
#******************************************************