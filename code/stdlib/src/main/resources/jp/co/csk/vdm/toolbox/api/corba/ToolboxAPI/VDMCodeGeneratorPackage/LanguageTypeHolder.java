package jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMCodeGeneratorPackage;

/**
* jp/co/csk/vdm/toolbox/api/corba/ToolboxAPI/VDMCodeGeneratorPackage/LanguageTypeHolder.java .
* IDL-to-Java コンパイラ (ポータブル), バージョン "3.1" で生成
* 生成元: corba_api.idl
* 2009年3月16日 10時22分53秒 JST
*/


// Default value = false;
public final class LanguageTypeHolder implements org.omg.CORBA.portable.Streamable
{
  public jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMCodeGeneratorPackage.LanguageType value = null;

  public LanguageTypeHolder ()
  {
  }

  public LanguageTypeHolder (jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMCodeGeneratorPackage.LanguageType initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMCodeGeneratorPackage.LanguageTypeHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMCodeGeneratorPackage.LanguageTypeHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMCodeGeneratorPackage.LanguageTypeHelper.type ();
  }

}
