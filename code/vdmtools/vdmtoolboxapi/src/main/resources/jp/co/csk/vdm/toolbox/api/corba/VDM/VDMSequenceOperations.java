package jp.co.csk.vdm.toolbox.api.corba.VDM;


/**
* jp/co/csk/vdm/toolbox/api/corba/VDM/VDMSequenceOperations.java .
* IDL-to-Java コンパイラ (ポータブル), バージョン "3.1" で生成
* 生成元: metaiv_idl.idl
* 2008年4月21日 15時58分01秒 JST
*/

public interface VDMSequenceOperations  extends jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGenericOperations
{
  jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric Index (int i) throws jp.co.csk.vdm.toolbox.api.corba.VDM.VDMError;
  jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric Hd () throws jp.co.csk.vdm.toolbox.api.corba.VDM.VDMError;
  jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSequence Tl () throws jp.co.csk.vdm.toolbox.api.corba.VDM.VDMError;
  void ImpTl () throws jp.co.csk.vdm.toolbox.api.corba.VDM.VDMError;
  void RemElem (int i) throws jp.co.csk.vdm.toolbox.api.corba.VDM.VDMError;
  int Length ();
  boolean GetString (org.omg.CORBA.StringHolder s);
  boolean IsEmpty ();
  void ImpAppend (jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric g) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;
  void ImpModify (int i, jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric g) throws jp.co.csk.vdm.toolbox.api.corba.VDM.VDMError, jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;
  void ImpPrepend (jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric g) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;
  void ImpConc (jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSequence s) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;
  jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSet Elems ();
  short First (jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGenericHolder g);
  short Next (jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGenericHolder g);
} // interface VDMSequenceOperations
