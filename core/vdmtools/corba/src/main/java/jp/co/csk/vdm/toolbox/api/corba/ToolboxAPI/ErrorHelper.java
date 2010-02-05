package jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI;


/**
* jp/co/csk/vdm/toolbox/api/corba/ToolboxAPI/ErrorHelper.java .
* IDL-to-Java コンパイラ (ポータブル), バージョン "3.1" で生成
* 生成元: corba_api.idl
* 2009年3月16日 10時22分53秒 JST
*/

abstract public class ErrorHelper
{
  private static String  _id = "IDL:ToolboxAPI/Error:1.0";

  public static void insert (org.omg.CORBA.Any a, jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.Error that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.Error extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  private static boolean __active = false;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      synchronized (org.omg.CORBA.TypeCode.class)
      {
        if (__typeCode == null)
        {
          if (__active)
          {
            return org.omg.CORBA.ORB.init().create_recursive_tc ( _id );
          }
          __active = true;
          org.omg.CORBA.StructMember[] _members0 = new org.omg.CORBA.StructMember [4];
          org.omg.CORBA.TypeCode _tcOf_members0 = null;
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.FileNameHelper.id (), "FileName", _tcOf_members0);
          _members0[0] = new org.omg.CORBA.StructMember (
            "fname",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_ushort);
          _members0[1] = new org.omg.CORBA.StructMember (
            "line",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_ushort);
          _members0[2] = new org.omg.CORBA.StructMember (
            "col",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _members0[3] = new org.omg.CORBA.StructMember (
            "msg",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_struct_tc (jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ErrorHelper.id (), "Error", _members0);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.Error read (org.omg.CORBA.portable.InputStream istream)
  {
    jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.Error value = new jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.Error ();
    value.fname = istream.read_string ();
    value.line = istream.read_ushort ();
    value.col = istream.read_ushort ();
    value.msg = istream.read_string ();
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.Error value)
  {
    ostream.write_string (value.fname);
    ostream.write_ushort (value.line);
    ostream.write_ushort (value.col);
    ostream.write_string (value.msg);
  }

}
