This model is made by Hugo Macedo as a part of his MSc thesis of a
pacemaker according to the grand challenge provided by Boston
Scientific in this area. This is the last of a series of VDM models
of the pacemaker and it incorporates a number of modes for the 
pacemaker. More information can be found in:

Hugo Macedo, Validating and Understanding Boston Scientific Pacemaker
Requirements, MSc thesis, Minho University, Portugal, October 2007.

Hugo Daniel Macedo, Peter Gorm Larsen and John Fitzgerald, Incremental 
Development of a Distributed Real-Time Model of a Cardiac Pacing System 
using VDM, In FM 2008: Formal Methods, 15th International Symposium on 
Formal Methods, Eds, Jorge Cuellar and Tom Maibaum and Kaisa Sere, 2008,
Springer-Verlag, Lecture Notes in Computer Science 5014, pp. 181--197.

#******************************************************
#  AUTOMATED TEST SETTINGS
#------------------------------------------------------
#AUTHOR= Hugo Macedo
#LIB= IO
#LANGUAGE_VERSION=classic
#INV_CHECKS=true
#POST_CHECKS=true
#PRE_CHECKS=true
#DYNAMIC_TYPE_CHECKS=true
#SUPPRESS_WARNINGS=false
#ENTRY_POINT=new World("tests/scenarioGoodHeart.arg",<DOO>).Run()
#ENTRY_POINT=new World("tests/scenarioDoubleHeart.arg",<DOO>).Run()
#ENTRY_POINT=new World("tests/scenarioBrokenHeart.arg",<DOO>).Run()
#ENTRY_POINT=new World("tests/scenarioSometimesHeart.arg",<DOO>).Run()
#ENTRY_POINT=new World("tests/scenarioGoodHeart.arg",<AOO>).Run()
#ENTRY_POINT=new World("tests/scenarioDoubleHeart.arg",<AOO>).Run()
#ENTRY_POINT=new World("tests/scenarioBrokenHeart.arg",<AOO>).Run()
#ENTRY_POINT=new World("tests/scenarioSometimesHeart.arg",<AOO>).Run()
#ENTRY_POINT=new World("tests/scenarioGoodHeart.arg",<AAI>).Run()
#ENTRY_POINT=new World("tests/scenarioDoubleHeart.arg",<AAI>).Run()
#ENTRY_POINT=new World("tests/scenarioBrokenHeart.arg",<AAI>).Run()
#ENTRY_POINT=new World("tests/scenarioSometimesHeart.arg",<AAI>).Run()
#ENTRY_POINT=new World("tests/scenarioGoodHeart.arg",<DDD>).Run()
#ENTRY_POINT=new World("tests/scenarioDoubleHeart.arg",<DDD>).Run()
#ENTRY_POINT=new World("tests/scenarioBrokenHeart.arg",<DDD>).Run()
#ENTRY_POINT=new World("tests/scenarioSometimesHeart.arg",<DDD>).Run()
#EXPECTED_RESULT=NO_ERROR_TYPE_CHECK
#******************************************************