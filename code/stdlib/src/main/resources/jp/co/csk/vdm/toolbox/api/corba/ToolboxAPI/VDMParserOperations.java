package jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI;


/**
* jp/co/csk/vdm/toolbox/api/corba/ToolboxAPI/VDMParserOperations.java .
* IDL-to-Java コンパイラ (ポータブル), バージョン "3.1" で生成
* 生成元: corba_api.idl
* 2009年3月16日 10時22分53秒 JST
*/

public interface VDMParserOperations 
{
  boolean Parse (String name) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;

  // If FileName does not exist an exception is raised.
  boolean ParseList (String[] names) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;
} // interface VDMParserOperations
