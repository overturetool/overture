package jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI;


/**
* jp/co/csk/vdm/toolbox/api/corba/ToolboxAPI/_VDMApplicationStub.java .
* IDL-to-Java コンパイラ (ポータブル), バージョン "3.1" で生成
* 生成元: corba_api.idl
* 2008年4月21日 15時58分02秒 JST
*/

public class _VDMApplicationStub extends org.omg.CORBA.portable.ObjectImpl implements jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMApplication
{

  public jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ToolType Tool ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_get_Tool", true);
                $in = _invoke ($out);
                jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ToolType $result = jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ToolTypeHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return Tool (        );
            } finally {
                _releaseReply ($in);
            }
  } // Tool


  // VDM-SL Toolbox or the VDM++ Toolbox.
  public short Register ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("Register", true);
                $in = _invoke ($out);
                short $result = jp.co.csk.vdm.toolbox.api.corba.VDM.ClientIDHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return Register (        );
            } finally {
                _releaseReply ($in);
            }
  } // Register


  // any calls towards the server.
  public void Unregister (short id)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("Unregister", true);
                jp.co.csk.vdm.toolbox.api.corba.VDM.ClientIDHelper.write ($out, id);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                Unregister (id        );
            } finally {
                _releaseReply ($in);
            }
  } // Unregister


  // the server to free any resources associated with the client.
  public jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMProject GetProject ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("GetProject", true);
                $in = _invoke ($out);
                jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMProject $result = jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMProjectHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return GetProject (        );
            } finally {
                _releaseReply ($in);
            }
  } // GetProject

  public jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMInterpreter GetInterpreter ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("GetInterpreter", true);
                $in = _invoke ($out);
                jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMInterpreter $result = jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMInterpreterHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return GetInterpreter (        );
            } finally {
                _releaseReply ($in);
            }
  } // GetInterpreter

  public jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMCodeGenerator GetCodeGenerator ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("GetCodeGenerator", true);
                $in = _invoke ($out);
                jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMCodeGenerator $result = jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMCodeGeneratorHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return GetCodeGenerator (        );
            } finally {
                _releaseReply ($in);
            }
  } // GetCodeGenerator

  public jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMParser GetParser ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("GetParser", true);
                $in = _invoke ($out);
                jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMParser $result = jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMParserHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return GetParser (        );
            } finally {
                _releaseReply ($in);
            }
  } // GetParser

  public jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMTypeChecker GetTypeChecker ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("GetTypeChecker", true);
                $in = _invoke ($out);
                jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMTypeChecker $result = jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMTypeCheckerHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return GetTypeChecker (        );
            } finally {
                _releaseReply ($in);
            }
  } // GetTypeChecker

  public jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMPrettyPrinter GetPrettyPrinter ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("GetPrettyPrinter", true);
                $in = _invoke ($out);
                jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMPrettyPrinter $result = jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMPrettyPrinterHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return GetPrettyPrinter (        );
            } finally {
                _releaseReply ($in);
            }
  } // GetPrettyPrinter

  public jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMErrors GetErrorHandler ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("GetErrorHandler", true);
                $in = _invoke ($out);
                jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMErrors $result = jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMErrorsHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return GetErrorHandler (        );
            } finally {
                _releaseReply ($in);
            }
  } // GetErrorHandler

  public jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMModuleRepos GetModuleRepos ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("GetModuleRepos", true);
                $in = _invoke ($out);
                jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMModuleRepos $result = jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMModuleReposHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return GetModuleRepos (        );
            } finally {
                _releaseReply ($in);
            }
  } // GetModuleRepos

  public jp.co.csk.vdm.toolbox.api.corba.VDM.VDMFactory GetVDMFactory ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("GetVDMFactory", true);
                $in = _invoke ($out);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMFactory $result = jp.co.csk.vdm.toolbox.api.corba.VDM.VDMFactoryHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return GetVDMFactory (        );
            } finally {
                _releaseReply ($in);
            }
  } // GetVDMFactory

  public void PushTag (short id)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("PushTag", true);
                jp.co.csk.vdm.toolbox.api.corba.VDM.ClientIDHelper.write ($out, id);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                PushTag (id        );
            } finally {
                _releaseReply ($in);
            }
  } // PushTag

  public void DestroyTag (short id) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("DestroyTag", true);
                jp.co.csk.vdm.toolbox.api.corba.VDM.ClientIDHelper.write ($out, id);
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
                DestroyTag (id        );
            } finally {
                _releaseReply ($in);
            }
  } // DestroyTag

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:ToolboxAPI/VDMApplication:1.0"};

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
} // class _VDMApplicationStub
