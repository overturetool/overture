package jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI;


/**
* jp/co/csk/vdm/toolbox/api/corba/ToolboxAPI/VDMModuleReposOperations.java .
* IDL-to-Java コンパイラ (ポータブル), バージョン "3.1" で生成
* 生成元: corba_api.idl
* 2009年3月16日 10時22分53秒 JST
*/

public interface VDMModuleReposOperations 
{
  short FilesOfModule (jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.FileListHolder files, String name) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;

  // consist of several files.
  String GetCurrentModule () throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;

  // is present
  void PopModule () throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;

  // Throws an Exception, if no module is on the stack
  void PushModule (String name) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;

  // if the specified module does not exist
  void Status (jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ModuleStatusHolder state, String name) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;

  //
  short SuperClasses (jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ClassListHolder classes, String name) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;
  short SubClasses (jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ClassListHolder classes, String name) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;

  //    unsigned short Uses(out ClassList classes, in ClassName name)
  short UsesOf (jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ClassListHolder classes, String name) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;
  short UsedBy (jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ClassListHolder classes, String name) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;
} // interface VDMModuleReposOperations
