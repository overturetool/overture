package jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI;


/**
* jp/co/csk/vdm/toolbox/api/corba/ToolboxAPI/VDMProjectOperations.java .
* IDL-to-Java コンパイラ (ポータブル), バージョン "3.1" で生成
* 生成元: corba_api.idl
* 2008年4月21日 15時58分02秒 JST
*/

public interface VDMProjectOperations 
{
  void New ();
  void Open (String name) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;
  void Save () throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;
  void SaveAs (String name) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;
  short GetModules (jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ModuleListHolder modules);
  short GetFiles (jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.FileListHolder files);
  void AddFile (String name) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;
  void RemoveFile (String name) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;
} // interface VDMProjectOperations
