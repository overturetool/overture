#include "metaiv.h"

extern "C" {
  Generic ExtGetCylinder(Sequence);
}

main() {

  Sequence sq; // empty sequence to call ExtGetCylinder with.
  Generic result;

  result = ExtGetCylinder(sq);
}
  
