package jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI;


/**
* jp/co/csk/vdm/toolbox/api/corba/ToolboxAPI/VDMTypeCheckerOperations.java .
* IDL-to-Java コンパイラ (ポータブル), バージョン "3.1" で生成
* 生成元: corba_api.idl
* 2009年3月16日 10時22分53秒 JST
*/

public interface VDMTypeCheckerOperations 
{
  boolean DefTypeCheck ();
  void DefTypeCheck (boolean newDefTypeCheck);

  // false the specification will be 'pos' type checked.
  boolean ExtendedTypeCheck ();

  // false the specification will be 'pos' type checked.
  void ExtendedTypeCheck (boolean newExtendedTypeCheck);

  // Default value = false;
  boolean TypeCheck (String name) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;

  // type check succeeded.
  boolean TypeCheckList (String[] names) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;
} // interface VDMTypeCheckerOperations
