package jp.co.csk.vdm.toolbox.api.corba.VDM;


/**
* jp/co/csk/vdm/toolbox/api/corba/VDM/VDMGenericOperations.java .
* IDL-to-Java コンパイラ (ポータブル), バージョン "3.1" で生成
* 生成元: metaiv_idl.idl
* 2008年4月21日 15時58分01秒 JST
*/

public interface VDMGenericOperations 
{
  String ToAscii ();
  boolean IsNil ();
  boolean IsChar ();
  boolean IsNumeric ();
  boolean IsQuote ();
  boolean IsTuple ();
  boolean IsRecord ();
  boolean IsSet ();
  boolean IsMap ();
  boolean IsText ();
  boolean IsToken ();
  boolean IsBool ();
  boolean IsSequence ();
  boolean IsObjectRef ();
  void Destroy () throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;

  // be released.
  byte[] GetCPPValue ();

  // iterating through a large VDM value.
  jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric Clone ();
} // interface VDMGenericOperations
