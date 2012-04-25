package jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI;


/**
* jp/co/csk/vdm/toolbox/api/corba/ToolboxAPI/VDMErrorsOperations.java .
* IDL-to-Java コンパイラ (ポータブル), バージョン "3.1" で生成
* 生成元: corba_api.idl
* 2009年3月16日 10時22分53秒 JST
*/

public interface VDMErrorsOperations 
{
  short NumErr ();
  short NumWarn ();

  // action.
  short GetErrors (jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ErrorListHolder err);
  short GetWarnings (jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ErrorListHolder err);
} // interface VDMErrorsOperations
