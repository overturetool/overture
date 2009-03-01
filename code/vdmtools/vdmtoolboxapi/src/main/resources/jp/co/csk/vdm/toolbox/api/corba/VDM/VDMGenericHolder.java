package jp.co.csk.vdm.toolbox.api.corba.VDM;

/**
* jp/co/csk/vdm/toolbox/api/corba/VDM/VDMGenericHolder.java .
* IDL-to-Java コンパイラ (ポータブル), バージョン "3.1" で生成
* 生成元: metaiv_idl.idl
* 2008年4月21日 15時58分01秒 JST
*/

public final class VDMGenericHolder implements org.omg.CORBA.portable.Streamable
{
  public jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric value = null;

  public VDMGenericHolder ()
  {
  }

  public VDMGenericHolder (jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGenericHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGenericHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGenericHelper.type ();
  }

}
