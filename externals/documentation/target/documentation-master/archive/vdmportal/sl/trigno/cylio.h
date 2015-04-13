//----------------------------------------------------------------------------
// cylio.h        Definition of the structure used.
//----------------------------------------------------------------------------
struct CircularCylinder {
  float rad;
  float height;
  float slope;
};

typedef struct CircularCylinder CircCyl;

extern CircCyl GetCircCyl();
extern void ShowCircCylVol(CircCyl cylinder, float volume);
