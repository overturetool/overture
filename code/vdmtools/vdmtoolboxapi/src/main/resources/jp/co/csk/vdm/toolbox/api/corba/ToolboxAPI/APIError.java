package jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI;


/**
* jp/co/csk/vdm/toolbox/api/corba/ToolboxAPI/APIError.java .
* IDL-to-Java コンパイラ (ポータブル), バージョン "3.1" で生成
* 生成元: metaiv_idl.idl
* 2008年4月21日 15時58分00秒 JST
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
