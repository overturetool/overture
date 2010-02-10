
In this specification a model of interlocking systems is presented,
and it is describe how the model may be validated by
simulation. Station topologies are modelled by graphs in which the
nodes denote track segments, and the edges denote connectivity for
train traffic. Points and signals are modelled by annotations on the
edges, thereby restricting the driving possibilities. We define the
safe station states as predicates on the graph, and present a first
step towards an implementation of these predicates. The model
development illustrates how concepts may be captured and validated for
a non-trivial system. This work was conducted by Kirsten Mark Hansen
work for the Danish State Railways.


#******************************************************
#  AUTOMATED TEST SETTINGS
#------------------------------------------------------
#LANGUAGE_VERSION=classic
#INV_CHECKS=true
#POST_CHECKS=true
#PRE_CHECKS=true
#DYNAMIC_TYPE_CHECKS=true
#SUPPRESS_WARNINGS=false
#ENTRY_POINT=
#EXPECTED_RESULT=NO_ERROR_TYPE_CHECK
#******************************************************