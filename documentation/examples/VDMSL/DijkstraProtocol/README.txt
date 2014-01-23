This small specification is based on a protocol made
by Dijkstra for termination of different processes in
a distributed setting (see [Dijkstra83]). This academic
example was used at a Dagstuhl workshop about "Integration 
of Tools for Rigorous Software Construction and Analysis"
(http://www.dagstuhl.de/de/programm/kalender/semhp/?semnr=13372)
The algorithm here was used to compare different formal
notations including VDM-SL.

[Dijkstra83] EWD 840: "Derivation of a termination detection 
algorithm for distributed computations" was published as
Information Processing Letters 16: 217–219, 1983. See
https://www.cs.utexas.edu/users/EWD/ewd08xx/EWD840.PDF 

#******************************************************
#  AUTOMATED TEST SETTINGS
#------------------------------------------------------
#LANGUAGE_VERSION=classic
#INV_CHECKS=true
#POST_CHECKS=true
#PRE_CHECKS=true
#DYNAMIC_TYPE_CHECKS=true
#SUPPRESS_WARNINGS=false
#EXPECTED_RESULT=NO_ERROR_TYPE_CHECK
#LIB=MATH
#ENTRY_POINT=Dijkstra`Main()
#ENTRY_POINT=Dijkstra`Main2()
#EXPECTED_RESULT=NO_ERROR_TYPE_CHECK
#AUTHOR= Peter Gorm Larsen
#******************************************************
