package jp.co.csk.vdm.toolbox.api.corba.VDM;


/**
* jp/co/csk/vdm/toolbox/api/corba/VDM/VDMFactoryOperations.java .
* IDL-to-Java コンパイラ (ポータブル), バージョン "3.1" で生成
* 生成元: metaiv_idl.idl
* 2008年4月21日 15時58分01秒 JST
*/

public interface VDMFactoryOperations 
{
  jp.co.csk.vdm.toolbox.api.corba.VDM.VDMNumeric MkNumeric (short id, double d);
  jp.co.csk.vdm.toolbox.api.corba.VDM.VDMBool MkBool (short id, boolean b);
  jp.co.csk.vdm.toolbox.api.corba.VDM.VDMNil MkNil (short id);
  jp.co.csk.vdm.toolbox.api.corba.VDM.VDMQuote MkQuote (short id, String s);
  jp.co.csk.vdm.toolbox.api.corba.VDM.VDMChar MkChar (short id, char c);
  jp.co.csk.vdm.toolbox.api.corba.VDM.VDMText MkText (short id, String s);
  jp.co.csk.vdm.toolbox.api.corba.VDM.VDMToken MkToken (short id, String s);
  jp.co.csk.vdm.toolbox.api.corba.VDM.VDMMap MkMap (short id);
  jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSequence MkSequence (short id);
  jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSet MkSet (short id);

  //    raises (ToolboxAPI::APIError);
  jp.co.csk.vdm.toolbox.api.corba.VDM.VDMTuple MkTuple (short id, int length);
  jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric FromCPPValue (short id, byte[] cppvalue);
} // interface VDMFactoryOperations
