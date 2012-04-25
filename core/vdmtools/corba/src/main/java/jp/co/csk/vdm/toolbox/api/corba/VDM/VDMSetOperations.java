package jp.co.csk.vdm.toolbox.api.corba.VDM;


/**
* jp/co/csk/vdm/toolbox/api/corba/VDM/VDMSetOperations.java .
* IDL-to-Java コンパイラ (ポータブル), バージョン "3.1" で生成
* 生成元: metaiv_idl.idl
* 2009年3月16日 10時22分52秒 JST
*/

public interface VDMSetOperations  extends jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGenericOperations
{
  void Insert (jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric g);
  int Card ();
  boolean IsEmpty ();
  boolean InSet (jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric g);
  void ImpUnion (jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSet s);
  void ImpIntersect (jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSet s);
  jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric GetElem () throws jp.co.csk.vdm.toolbox.api.corba.VDM.VDMError;
  void RemElem (jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric g) throws jp.co.csk.vdm.toolbox.api.corba.VDM.VDMError;
  boolean SubSet (jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSet s);
  void ImpDiff (jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSet s);
  short First (jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGenericHolder g);
  short Next (jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGenericHolder g);
} // interface VDMSetOperations
