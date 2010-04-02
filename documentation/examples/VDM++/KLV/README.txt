This example describes a VDM++ specification of a KLV system. The
purpose of the KLV system is to provide a continuous monitoring of the
speed of a train. The VDM++ specification is inspired by a KLV
description provided to the EP26538 FMERail project as case study by
Atelier B.  This model shows an example of how an informal description
can be translated into a precise model that together with a graphical
front-end can be used to ensure that the customer and the developer
have a common interpretation of the system under development.

The focus of the model is on the logic of the KLV systems when a train
meets speed restriction beacons along the tracks, i.e. on events that
triggers the KLV system. Issues such as how to determine whether a
beacon has been met within a certain distance calculated from speed
has been abstracted in away in the current model. They could be issues
to extend the model with.

#******************************************************
#  AUTOMATED TEST SETTINGS
#------------------------------------------------------
#AUTHOR=Niels Kirkegaard
#LANGUAGE_VERSION=classic
#INV_CHECKS=true
#POST_CHECKS=true
#PRE_CHECKS=true
#DYNAMIC_TYPE_CHECKS=true
#SUPPRESS_WARNINGS=false
#ENTRY_POINT= new UseKLV().Seq1()
#ENTRY_POINT= 
#EXPECTED_RESULT=NO_ERROR_TYPE_CHECK
#******************************************************
