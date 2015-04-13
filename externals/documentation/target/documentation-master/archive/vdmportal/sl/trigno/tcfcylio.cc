//-------------------------------------------------------------------------  
// tcfcylio.cc    type conversion functions for circular cylinder io
//-------------------------------------------------------------------------

#include "metaiv.h"
#include "cylio.h"

extern "C" {
  Map NameMap();
  Generic ExtGetCylinder(Sequence sq1);
  void ExtShowCircCylVol(Sequence sq1);
}

Map NameMap() {
  Map map;
  map.Insert(Text("CYLINDER`CircCyl"),Int(1));
  return map;
}

Generic ExtGetCylinder(Sequence sq1) {
  CircCyl cyl;
  Record Rc(1, 3);
  
  cyl = GetCircCyl();   // input of cylinder dimension
  Rc.SetField(1, (Real)cyl.rad); // conversion in VDM C++ record class
  Rc.SetField(2, (Real)cyl.height);
  Rc.SetField(3, (Real)cyl.slope);
  return(Rc);    // return Record to the interpreter process               
}

void ExtShowCircCylVol(Sequence sq1) {
  CircCyl cyl;
  Record Rc(1,3);
  float vol;
  
  // extract cylinder dimension and volume from sequence
  Rc =  sq1[1];
  vol = (Real) sq1[2];
  
  // convert Record in a C++ structure
  cyl.rad = (Real) Rc.GetField(1);
  cyl.height = (Real) Rc.GetField(2);
  cyl.slope = (Real) Rc.GetField(3);
  
  ShowCircCylVol(cyl, vol); //make output
  return;
}

