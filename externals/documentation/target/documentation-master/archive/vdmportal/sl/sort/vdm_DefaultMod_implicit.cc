/***
*  * WHAT
*  *    Handwritten implementation of `merge sort'
*  * ID
*  *    $Id: vdm_DefaultMod_implicit.cc,v 1.1 2005/11/28 07:14:45 vdmtools Exp $
*  * PROJECT
*  *    Toolbox
*  * COPYRIGHT
*  *    (C) 1994 IFAD, Denmark
***/

static Sequence Merge(Sequence, Sequence);

Sequence vdm_DefaultMod_ImplSort(Sequence l) {
  int len = l.Length();
  if (len <= 1)
    return l;
  else {
    int l2 = len/2;
    Sequence l_l, l_r;
    for (int i=1; i<=l2; i++)
      l_l.ImpAppend(l[i]);
    for (; i<=len; i++)
      l_r.ImpAppend(l[i]);
    return Merge(vdm_DefaultMod_ImplSort(l_l), vdm_DefaultMod_ImplSort(l_r));
  }
}

Sequence Merge(Sequence l1, Sequence l2)
{
  if (l1.Length() == 0)
    return l2;
  else if (l2.Length() == 0)
    return l1;
  else {
    Sequence res;
    Real e1 = l1.Hd();
    Real e2 = l2.Hd();
    if (e1 <= e2)
      return res.ImpAppend(e1).ImpConc(Merge(l1.ImpTl(), l2));
    else
      return res.ImpAppend(e2).ImpConc(Merge(l1, l2.ImpTl()));
  }
}
