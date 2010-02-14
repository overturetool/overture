This VDM model is made by Peter Gorm Larsen as an exploration of how
the looseness in a subset of VDM-SL. So this is illustrating how it is
possible to explore all models is a simple fashion including the
possibility of recursive functions where looseness is involved inside
each recursive call. A paper about this work have been published as:

Peter Gorm Larsen, Evaluation of Underdetermined Explicit Expressions,
Formal Methods Europe'94: Industrial Benefit of Formal Methods,
Springer Verlag, October 1994.

#******************************************************
#  AUTOMATED TEST SETTINGS
#------------------------------------------------------
#LANGUAGE_VERSION=classic
#INV_CHECKS=true
#POST_CHECKS=true
#PRE_CHECKS=true
#DYNAMIC_TYPE_CHECKS=true
#SUPPRESS_WARNINGS=false
#ENTRY_POINT=DEFAULT`LooseEvalExpr(mk_NumLit(8))
#ENTRY_POINT=DEFAULT`LooseEvalExpr(expr)
#EXPECTED_RESULT=NO_ERROR_TYPE_CHECK
#******************************************************
