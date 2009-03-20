package jp.co.csk.vdm.toolbox.api.corba.VDM;


/**
* jp/co/csk/vdm/toolbox/api/corba/VDM/VDMError.java .
* IDL-to-Java コンパイラ (ポータブル), バージョン "3.1" で生成
* 生成元: metaiv_idl.idl
* 2009年3月16日 10時22分52秒 JST
*/

public final class VDMError extends org.omg.CORBA.UserException
{
  public short err = (short)0;

  public VDMError ()
  {
    super(VDMErrorHelper.id());
  } // ctor

  public VDMError (short _err)
  {
    super(VDMErrorHelper.id());
    err = _err;
  } // ctor


  public VDMError (String $reason, short _err)
  {
    super(VDMErrorHelper.id() + "  " + $reason);
    err = _err;
  } // ctor

} // class VDMError
