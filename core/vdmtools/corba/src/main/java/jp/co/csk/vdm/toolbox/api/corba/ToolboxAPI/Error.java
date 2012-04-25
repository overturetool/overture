package jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI;


/**
* jp/co/csk/vdm/toolbox/api/corba/ToolboxAPI/Error.java .
* IDL-to-Java コンパイラ (ポータブル), バージョン "3.1" で生成
* 生成元: corba_api.idl
* 2009年3月16日 10時22分53秒 JST
*/

public final class Error implements org.omg.CORBA.portable.IDLEntity
{
  public String fname = null;
  public short line = (short)0;
  public short col = (short)0;
  public String msg = null;

  public Error ()
  {
  } // ctor

  public Error (String _fname, short _line, short _col, String _msg)
  {
    fname = _fname;
    line = _line;
    col = _col;
    msg = _msg;
  } // ctor

} // class Error
