package jp.co.csk.vdm.toolbox.api.corba.VDM;


/**
* jp/co/csk/vdm/toolbox/api/corba/VDM/VDMFactoryHelper.java .
* IDL-to-Java コンパイラ (ポータブル), バージョン "3.1" で生成
* 生成元: metaiv_idl.idl
* 2009年3月16日 10時22分52秒 JST
*/

abstract public class VDMFactoryHelper
{
  private static String  _id = "IDL:VDM/VDMFactory:1.0";

  public static void insert (org.omg.CORBA.Any a, jp.co.csk.vdm.toolbox.api.corba.VDM.VDMFactory that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static jp.co.csk.vdm.toolbox.api.corba.VDM.VDMFactory extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (jp.co.csk.vdm.toolbox.api.corba.VDM.VDMFactoryHelper.id (), "VDMFactory");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static jp.co.csk.vdm.toolbox.api.corba.VDM.VDMFactory read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_VDMFactoryStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, jp.co.csk.vdm.toolbox.api.corba.VDM.VDMFactory value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static jp.co.csk.vdm.toolbox.api.corba.VDM.VDMFactory narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof jp.co.csk.vdm.toolbox.api.corba.VDM.VDMFactory)
      return (jp.co.csk.vdm.toolbox.api.corba.VDM.VDMFactory)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      jp.co.csk.vdm.toolbox.api.corba.VDM._VDMFactoryStub stub = new jp.co.csk.vdm.toolbox.api.corba.VDM._VDMFactoryStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

  public static jp.co.csk.vdm.toolbox.api.corba.VDM.VDMFactory unchecked_narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof jp.co.csk.vdm.toolbox.api.corba.VDM.VDMFactory)
      return (jp.co.csk.vdm.toolbox.api.corba.VDM.VDMFactory)obj;
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      jp.co.csk.vdm.toolbox.api.corba.VDM._VDMFactoryStub stub = new jp.co.csk.vdm.toolbox.api.corba.VDM._VDMFactoryStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}
