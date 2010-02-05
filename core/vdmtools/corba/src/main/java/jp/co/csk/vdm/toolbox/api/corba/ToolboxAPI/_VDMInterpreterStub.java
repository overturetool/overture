package jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI;


/**
* jp/co/csk/vdm/toolbox/api/corba/ToolboxAPI/_VDMInterpreterStub.java .
* IDL-to-Java コンパイラ (ポータブル), バージョン "3.1" で生成
* 生成元: corba_api.idl
* 2009年3月16日 10時22分53秒 JST
*/

public class _VDMInterpreterStub extends org.omg.CORBA.portable.ObjectImpl implements jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMInterpreter
{

  public boolean DynTypeCheck ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_get_DynTypeCheck", true);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return DynTypeCheck (        );
            } finally {
                _releaseReply ($in);
            }
  } // DynTypeCheck

  public void DynTypeCheck (boolean newDynTypeCheck)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_set_DynTypeCheck", true);
                $out.write_boolean (newDynTypeCheck);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                DynTypeCheck (newDynTypeCheck        );
            } finally {
                _releaseReply ($in);
            }
  } // DynTypeCheck


  // Enable dynamic type check. Default value = false.
  public boolean DynInvCheck ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_get_DynInvCheck", true);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return DynInvCheck (        );
            } finally {
                _releaseReply ($in);
            }
  } // DynInvCheck


  // Enable dynamic type check. Default value = false.
  public void DynInvCheck (boolean newDynInvCheck)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_set_DynInvCheck", true);
                $out.write_boolean (newDynInvCheck);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                DynInvCheck (newDynInvCheck        );
            } finally {
                _releaseReply ($in);
            }
  } // DynInvCheck


  // automatically set to true also.
  public boolean DynPreCheck ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_get_DynPreCheck", true);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return DynPreCheck (        );
            } finally {
                _releaseReply ($in);
            }
  } // DynPreCheck


  // automatically set to true also.
  public void DynPreCheck (boolean newDynPreCheck)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_set_DynPreCheck", true);
                $out.write_boolean (newDynPreCheck);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                DynPreCheck (newDynPreCheck        );
            } finally {
                _releaseReply ($in);
            }
  } // DynPreCheck


  // Enable checking of pre conditions. Default value = false.
  public boolean DynPostCheck ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_get_DynPostCheck", true);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return DynPostCheck (        );
            } finally {
                _releaseReply ($in);
            }
  } // DynPostCheck


  // Enable checking of pre conditions. Default value = false.
  public void DynPostCheck (boolean newDynPostCheck)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_set_DynPostCheck", true);
                $out.write_boolean (newDynPostCheck);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                DynPostCheck (newDynPostCheck        );
            } finally {
                _releaseReply ($in);
            }
  } // DynPostCheck


  // Enable checking of post conditions. Default value = false.
  public boolean PPOfValues ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_get_PPOfValues", true);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return PPOfValues (        );
            } finally {
                _releaseReply ($in);
            }
  } // PPOfValues


  // Enable checking of post conditions. Default value = false.
  public void PPOfValues (boolean newPPOfValues)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_set_PPOfValues", true);
                $out.write_boolean (newPPOfValues);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                PPOfValues (newPPOfValues        );
            } finally {
                _releaseReply ($in);
            }
  } // PPOfValues


  // Enable pretty printing of values. Default value = true.
  public boolean Verbose ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_get_Verbose", true);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return Verbose (        );
            } finally {
                _releaseReply ($in);
            }
  } // Verbose


  // Enable pretty printing of values. Default value = true.
  public void Verbose (boolean newVerbose)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_set_Verbose", true);
                $out.write_boolean (newVerbose);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                Verbose (newVerbose        );
            } finally {
                _releaseReply ($in);
            }
  } // Verbose


  // This attribute is false by default.
  public boolean Debug ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_get_Debug", true);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return Debug (        );
            } finally {
                _releaseReply ($in);
            }
  } // Debug


  // This attribute is false by default.
  public void Debug (boolean newDebug)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_set_Debug", true);
                $out.write_boolean (newDebug);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                Debug (newDebug        );
            } finally {
                _releaseReply ($in);
            }
  } // Debug


  // debugging.  Default value = false.
  public void Initialize () throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("Initialize", true);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:ToolboxAPI/APIError:1.0"))
                    throw jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIErrorHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                Initialize (        );
            } finally {
                _releaseReply ($in);
            }
  } // Initialize

  public jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric EvalExpression (short id, String expr) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("EvalExpression", true);
                jp.co.csk.vdm.toolbox.api.corba.VDM.ClientIDHelper.write ($out, id);
                $out.write_string (expr);
                $in = _invoke ($out);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric $result = jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGenericHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:ToolboxAPI/APIError:1.0"))
                    throw jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIErrorHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return EvalExpression (id, expr        );
            } finally {
                _releaseReply ($in);
            }
  } // EvalExpression


  // If a run-time error occurs an exception will be raised.
  public jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric Apply (short id, String f, jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSequence arg) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("Apply", true);
                jp.co.csk.vdm.toolbox.api.corba.VDM.ClientIDHelper.write ($out, id);
                $out.write_string (f);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSequenceHelper.write ($out, arg);
                $in = _invoke ($out);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric $result = jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGenericHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:ToolboxAPI/APIError:1.0"))
                    throw jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIErrorHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return Apply (id, f, arg        );
            } finally {
                _releaseReply ($in);
            }
  } // Apply


  // interfaces and methods provided in the VDM module.
  public void EvalCmd (String cmd) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("EvalCmd", true);
                $out.write_string (cmd);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:ToolboxAPI/APIError:1.0"))
                    throw jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIErrorHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                EvalCmd (cmd        );
            } finally {
                _releaseReply ($in);
            }
  } // EvalCmd


  // interpreter. An exception is thrown if....TBD!
  public int SetBreakPointByPos (String file, int line, int col) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("SetBreakPointByPos", true);
                $out.write_string (file);
                $out.write_long (line);
                $out.write_long (col);
                $in = _invoke ($out);
                int $result = $in.read_long ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:ToolboxAPI/APIError:1.0"))
                    throw jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIErrorHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return SetBreakPointByPos (file, line, col        );
            } finally {
                _releaseReply ($in);
            }
  } // SetBreakPointByPos


  // Used to set a breakpoint in a file at line:column
  public int SetBreakPointByName (String mod, String func) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("SetBreakPointByName", true);
                $out.write_string (mod);
                $out.write_string (func);
                $in = _invoke ($out);
                int $result = $in.read_long ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:ToolboxAPI/APIError:1.0"))
                    throw jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIErrorHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return SetBreakPointByName (mod, func        );
            } finally {
                _releaseReply ($in);
            }
  } // SetBreakPointByName


  // func is the name of the function
  public void DeleteBreakPoint (int num) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("DeleteBreakPoint", true);
                $out.write_long (num);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:ToolboxAPI/APIError:1.0"))
                    throw jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIErrorHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                DeleteBreakPoint (num        );
            } finally {
                _releaseReply ($in);
            }
  } // DeleteBreakPoint


  // used to delete a breakpoint
  public jp.co.csk.vdm.toolbox.api.corba.VDM.VDMTuple StartDebugging (short id, String expr) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("StartDebugging", true);
                jp.co.csk.vdm.toolbox.api.corba.VDM.ClientIDHelper.write ($out, id);
                $out.write_string (expr);
                $in = _invoke ($out);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMTuple $result = jp.co.csk.vdm.toolbox.api.corba.VDM.VDMTupleHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:ToolboxAPI/APIError:1.0"))
                    throw jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIErrorHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return StartDebugging (id, expr        );
            } finally {
                _releaseReply ($in);
            }
  } // StartDebugging


  // as a list of tokens
  public jp.co.csk.vdm.toolbox.api.corba.VDM.VDMTuple DebugStep (short id) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("DebugStep", true);
                jp.co.csk.vdm.toolbox.api.corba.VDM.ClientIDHelper.write ($out, id);
                $in = _invoke ($out);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMTuple $result = jp.co.csk.vdm.toolbox.api.corba.VDM.VDMTupleHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:ToolboxAPI/APIError:1.0"))
                    throw jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIErrorHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return DebugStep (id        );
            } finally {
                _releaseReply ($in);
            }
  } // DebugStep

  public jp.co.csk.vdm.toolbox.api.corba.VDM.VDMTuple DebugStepIn (short id) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("DebugStepIn", true);
                jp.co.csk.vdm.toolbox.api.corba.VDM.ClientIDHelper.write ($out, id);
                $in = _invoke ($out);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMTuple $result = jp.co.csk.vdm.toolbox.api.corba.VDM.VDMTupleHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:ToolboxAPI/APIError:1.0"))
                    throw jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIErrorHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return DebugStepIn (id        );
            } finally {
                _releaseReply ($in);
            }
  } // DebugStepIn

  public jp.co.csk.vdm.toolbox.api.corba.VDM.VDMTuple DebugSingleStep (short id) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("DebugSingleStep", true);
                jp.co.csk.vdm.toolbox.api.corba.VDM.ClientIDHelper.write ($out, id);
                $in = _invoke ($out);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMTuple $result = jp.co.csk.vdm.toolbox.api.corba.VDM.VDMTupleHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:ToolboxAPI/APIError:1.0"))
                    throw jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIErrorHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return DebugSingleStep (id        );
            } finally {
                _releaseReply ($in);
            }
  } // DebugSingleStep

  public jp.co.csk.vdm.toolbox.api.corba.VDM.VDMTuple DebugContinue (short id) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("DebugContinue", true);
                jp.co.csk.vdm.toolbox.api.corba.VDM.ClientIDHelper.write ($out, id);
                $in = _invoke ($out);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMTuple $result = jp.co.csk.vdm.toolbox.api.corba.VDM.VDMTupleHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:ToolboxAPI/APIError:1.0"))
                    throw jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIErrorHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return DebugContinue (id        );
            } finally {
                _releaseReply ($in);
            }
  } // DebugContinue

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:ToolboxAPI/VDMInterpreter:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }

  private void readObject (java.io.ObjectInputStream s) throws java.io.IOException
  {
     String str = s.readUTF ();
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.Object obj = org.omg.CORBA.ORB.init (args, props).string_to_object (str);
     org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate ();
     _set_delegate (delegate);
  }

  private void writeObject (java.io.ObjectOutputStream s) throws java.io.IOException
  {
     String[] args = null;
     java.util.Properties props = null;
     String str = org.omg.CORBA.ORB.init (args, props).object_to_string (this);
     s.writeUTF (str);
  }
} // class _VDMInterpreterStub
