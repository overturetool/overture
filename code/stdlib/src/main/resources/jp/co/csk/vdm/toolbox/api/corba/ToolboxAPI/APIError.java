package jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI;


/**
* jp/co/csk/vdm/toolbox/api/corba/ToolboxAPI/APIError.java .
* IDL-to-Java コンパイラ (ポータブル), バージョン "3.1" で生成
* 生成元: metaiv_idl.idl
* 2009年3月16日 10時22分52秒 JST
*/

public final class APIError extends org.omg.CORBA.UserException
{
  public String msg = null;

  public APIError ()
  {
    super(APIErrorHelper.id());
  } // ctor

  public APIError (String _msg)
  {
    super(APIErrorHelper.id());
    msg = _msg;
  } // ctor


  public APIError (String $reason, String _msg)
  {
    super(APIErrorHelper.id() + "  " + $reason);
    msg = _msg;
  } // ctor

} // class APIError
