package jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI;


/**
* jp/co/csk/vdm/toolbox/api/corba/ToolboxAPI/VDMPrettyPrinterOperations.java .
* IDL-to-Java コンパイラ (ポータブル), バージョン "3.1" で生成
* 生成元: corba_api.idl
* 2009年3月16日 10時22分53秒 JST
*/

public interface VDMPrettyPrinterOperations 
{
  boolean PrettyPrint (String name) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;
  boolean PrettyPrintList (String[] names) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;
} // interface VDMPrettyPrinterOperations
