This example comes from the VDM-SL book by John Fitzgerald and Peter Gorm
Larsen. It is the running example through the chapter about logic. 
Suppose we are asked to develop the software for a temperature
monitor for a reactor vessel in the plant. The monitor is connected to
a temperature sensor inside the vessel from which it receives a
reading(in degrees Celsius) every minute.

The monitor records the five most recent temperature readings in the
order in which they were received from the sensor.

#******************************************************
#  AUTOMATED TEST SETTINGS
#------------------------------------------------------
#AUTHOR= John Fitzgerald and Peter Gorm Larsen
#LANGUAGE_VERSION=classic
#INV_CHECKS=true
#POST_CHECKS=true
#PRE_CHECKS=true
#DYNAMIC_TYPE_CHECKS=true
#SUPPRESS_WARNINGS=false
#ENTRY_POINT=DEFAULT`OverLimit([4,2,8,555,123])
#EXPECTED_RESULT=NO_ERROR_TYPE_CHECK
#******************************************************