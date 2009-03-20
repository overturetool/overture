package jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI;


/**
* jp/co/csk/vdm/toolbox/api/corba/ToolboxAPI/VDMInterpreterOperations.java .
* IDL-to-Java コンパイラ (ポータブル), バージョン "3.1" で生成
* 生成元: corba_api.idl
* 2009年3月16日 10時22分53秒 JST
*/

public interface VDMInterpreterOperations 
{
  boolean DynTypeCheck ();
  void DynTypeCheck (boolean newDynTypeCheck);

  // Enable dynamic type check. Default value = false.
  boolean DynInvCheck ();

  // Enable dynamic type check. Default value = false.
  void DynInvCheck (boolean newDynInvCheck);

  // automatically set to true also.
  boolean DynPreCheck ();

  // automatically set to true also.
  void DynPreCheck (boolean newDynPreCheck);

  // Enable checking of pre conditions. Default value = false.
  boolean DynPostCheck ();

  // Enable checking of pre conditions. Default value = false.
  void DynPostCheck (boolean newDynPostCheck);

  // Enable checking of post conditions. Default value = false.
  boolean PPOfValues ();

  // Enable checking of post conditions. Default value = false.
  void PPOfValues (boolean newPPOfValues);

  // Enable pretty printing of values. Default value = true.
  boolean Verbose ();

  // Enable pretty printing of values. Default value = true.
  void Verbose (boolean newVerbose);

  // This attribute is false by default.
  boolean Debug ();

  // This attribute is false by default.
  void Debug (boolean newDebug);

  // debugging.  Default value = false.
  void Initialize () throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;
  jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric EvalExpression (short id, String expr) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;

  // If a run-time error occurs an exception will be raised.
  jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric Apply (short id, String f, jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSequence arg) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;

  // interfaces and methods provided in the VDM module.
  void EvalCmd (String cmd) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;

  // interpreter. An exception is thrown if....TBD!
  int SetBreakPointByPos (String file, int line, int col) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;

  // Used to set a breakpoint in a file at line:column
  int SetBreakPointByName (String mod, String func) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;

  // func is the name of the function
  void DeleteBreakPoint (int num) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;

  // used to delete a breakpoint
  jp.co.csk.vdm.toolbox.api.corba.VDM.VDMTuple StartDebugging (short id, String expr) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;

  // as a list of tokens
  jp.co.csk.vdm.toolbox.api.corba.VDM.VDMTuple DebugStep (short id) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;
  jp.co.csk.vdm.toolbox.api.corba.VDM.VDMTuple DebugStepIn (short id) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;
  jp.co.csk.vdm.toolbox.api.corba.VDM.VDMTuple DebugSingleStep (short id) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;
  jp.co.csk.vdm.toolbox.api.corba.VDM.VDMTuple DebugContinue (short id) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;
} // interface VDMInterpreterOperations
