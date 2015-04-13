//----------------------------------------------------------------------------
// cylio.cc        Circular Cylinder simple I/O functions
//----------------------------------------------------------------------------
#include <iostream.h>
#include "cylio.h"

CircCyl GetCircCyl() {
  CircCyl in;
    
  cout <<"\n\n Input of Circular Cylinder Dimensions";
  cout <<"\n  radius: ";
  cin >> in.rad;

  cout <<"  height: ";
  cin >> in.height;

  cout <<"  slope [rad]: ";
  cin >> in.slope;

  return in;
}


void ShowCircCylVol(CircCyl cyl, float volume) {
  cout << "\n\n Volume of Circular Cylinder\n\n";
  cout << "Dimensions: \n   radius: " << cyl.rad;
  cout << "\n   height: " << cyl.height;
  cout << "\n   slope: "<< cyl.slope << "\n\n";
  cout << " volume: " << volume<< "\n\n";
}
