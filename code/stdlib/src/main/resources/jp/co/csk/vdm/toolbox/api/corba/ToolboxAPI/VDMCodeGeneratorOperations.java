package jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI;


/**
* jp/co/csk/vdm/toolbox/api/corba/ToolboxAPI/VDMCodeGeneratorOperations.java .
* IDL-to-Java コンパイラ (ポータブル), バージョン "3.1" で生成
* 生成元: corba_api.idl
* 2008年4月21日 15時58分02秒 JST
*/

public interface VDMCodeGeneratorOperations 
{
  boolean GeneratePosInfo ();
  void GeneratePosInfo (boolean newGeneratePosInfo);

  // VDM++ Toolbox
  boolean GenerateCode (String name, jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMCodeGeneratorPackage.LanguageType targetLang) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;
  boolean GenerateCodeList (String[] names, jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMCodeGeneratorPackage.LanguageType targetLang) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;
} // interface VDMCodeGeneratorOperations
