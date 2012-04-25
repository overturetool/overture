package jp.co.csk.vdm.toolbox.api.corba.VDM;


/**
* jp/co/csk/vdm/toolbox/api/corba/VDM/VDMRecordOperations.java .
* IDL-to-Java コンパイラ (ポータブル), バージョン "3.1" で生成
* 生成元: metaiv_idl.idl
* 2009年3月16日 10時22分52秒 JST
*/

public interface VDMRecordOperations  extends jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGenericOperations
{
  void SetField (int i, jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric g) throws jp.co.csk.vdm.toolbox.api.corba.VDM.VDMError;
  jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric GetField (int i) throws jp.co.csk.vdm.toolbox.api.corba.VDM.VDMError;
  String GetTag () throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;
  boolean Is (String tag);
  int Length ();
} // interface VDMRecordOperations
