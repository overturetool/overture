//------------------------------------------------------------------------
// tcfmath.cc   type conversion functions for libmath.so
//------------------------------------------------------------------------
#include "metaiv.h"
#include <math.h>

extern "C" {
  Generic ExtCos(Sequence sq);
  Generic ExtSin(Sequence sq);
  Generic ExtPI ();
}

Generic ExtCos(Sequence sq) {
  return (Real( cos((Real)sq[1]) ));
}

Generic ExtSin(Sequence sq) {
  return (Real( sin((Real) sq[1]) ));
}

Generic ExtPI () {
  return(Real(M_PI));
}
