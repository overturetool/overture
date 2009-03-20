package jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI;


/**
* jp/co/csk/vdm/toolbox/api/corba/ToolboxAPI/ErrorListHelper.java .
* IDL-to-Java コンパイラ (ポータブル), バージョン "3.1" で生成
* 生成元: corba_api.idl
* 2009年3月16日 10時22分53秒 JST
*/

abstract public class ErrorListHelper
{
  private static String  _id = "IDL:ToolboxAPI/ErrorList:1.0";

  public static void insert (org.omg.CORBA.Any a, jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.Error[] that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.Error[] extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ErrorHelper.type ();
      __typeCode = org.omg.CORBA.ORB.init ().create_sequence_tc (0, __typeCode);
      __typeCode = org.omg.CORBA.ORB.init ().create_alias_tc (jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ErrorListHelper.id (), "ErrorList", __typeCode);
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.Error[] read (org.omg.CORBA.portable.InputStream istream)
  {
    jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.Error value[] = null;
    int _len0 = istream.read_long ();
    value = new jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.Error[_len0];
    for (int _o1 = 0;_o1 < value.length; ++_o1)
      value[_o1] = jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ErrorHelper.read (istream);
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.Error[] value)
  {
    ostream.write_long (value.length);
    for (int _i0 = 0;_i0 < value.length; ++_i0)
      jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ErrorHelper.write (ostream, value[_i0]);
  }

}
