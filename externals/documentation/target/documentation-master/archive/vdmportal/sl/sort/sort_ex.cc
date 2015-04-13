/***
*  * WHAT
*  *    Main C++ program for the VDM-SL sort example
*  * ID
*  *    $Id: sort_ex.cc,v 1.1 2005/11/28 07:14:45 vdmtools Exp $
*  * PROJECT
*  *    Toolbox
*  * COPYRIGHT
*  *    (C) 1994 IFAD, Denmark
***/

#include <fstream.h>
#include "metaiv.h"
#include "DefaultMod.h"

main() 
{
  // Initialize values in DefaultMod
  init_DefaultMod();

  // Constructing the value l = [0, -12, 45]
  Sequence l;
  l.ImpAppend(Int(0));
  l.ImpAppend(Int(-12));
  l.ImpAppend(Int(45));

  Sequence ll;
  Bool b;

  // l' := DoSort(l);
  cout << "Evaluating DoSort(" << l.ascii() << "):\n";
  ll = vdm_DefaultMod_DoSort(l);
  cout << ll.ascii() << "\n\n";
  
  // l' := ExplSort(l);
  cout << "Evaluating ExplSort(" << l.ascii() << "):\n";
  ll = vdm_DefaultMod_ExplSort(l);
  cout << ll.ascii() << "\n\n";
  
  // l' := ImplSort(l);
  cout << "Evaluating ImplSort(" << l.ascii() << "):\n";
  ll = vdm_DefaultMod_ImplSort(l);
  cout << ll.ascii() << "\n\n";

  // b  := post_ImplSort(l, l');
  cout << "Evaluating the post condition of ImplSort. \n"
       << "post_ImplSort(" << l.ascii() << ", "
       << ll.ascii() << "):\n";
  b = vdm_DefaultMod_post_ImplSort(l, ll);
  cout << b.ascii() << "\n\n";


  // b  := inv_PosReal(hd l);
  cout << "Evaluation the invariant of PosReal. \n"
       << "inv_PosReal(" << l.Hd().ascii() << "):\n";
  b = vdm_DefaultMod_inv_PosReal(l.Hd());
  cout << b.ascii() << "\n\n";

  // inv_PosReal(hd l');
  cout << "Evaluation the invariant of PosReal. \n"
       << "inv_PosReal(" << ll.Hd().ascii() << "):\n";
  b = vdm_DefaultMod_inv_PosReal(ll.Hd());
  cout << b.ascii() << "\n\n";
  
  // l' := MergeSort(l);
  // This will imply a run-time error!
  cout << "Evaluating MergeSort(" << l.ascii() << "):\n";
  ll = vdm_DefaultMod_MergeSort(l);
  cout << ll.ascii() << "\n\n";

}
