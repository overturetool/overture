package jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI;


/**
* jp/co/csk/vdm/toolbox/api/corba/ToolboxAPI/_VDMModuleReposStub.java .
* IDL-to-Java コンパイラ (ポータブル), バージョン "3.1" で生成
* 生成元: corba_api.idl
* 2008年4月21日 15時58分02秒 JST
*/

public class _VDMModuleReposStub extends org.omg.CORBA.portable.ObjectImpl implements jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMModuleRepos
{

  public short FilesOfModule (jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.FileListHolder files, String name) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("FilesOfModule", true);
                jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ModuleNameHelper.write ($out, name);
                $in = _invoke ($out);
                short $result = $in.read_ushort ();
                files.value = jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.FileListHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:ToolboxAPI/APIError:1.0"))
                    throw jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIErrorHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return FilesOfModule (files, name        );
            } finally {
                _releaseReply ($in);
            }
  } // FilesOfModule


  // consist of several files.
  public String GetCurrentModule () throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("GetCurrentModule", true);
                $in = _invoke ($out);
                String $result = jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ModuleNameHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:ToolboxAPI/APIError:1.0"))
                    throw jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIErrorHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return GetCurrentModule (        );
            } finally {
                _releaseReply ($in);
            }
  } // GetCurrentModule


  // is present
  public void PopModule () throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("PopModule", true);
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
                PopModule (        );
            } finally {
                _releaseReply ($in);
            }
  } // PopModule


  // Throws an Exception, if no module is on the stack
  public void PushModule (String name) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("PushModule", true);
                jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ModuleNameHelper.write ($out, name);
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
                PushModule (name        );
            } finally {
                _releaseReply ($in);
            }
  } // PushModule


  // if the specified module does not exist
  public void Status (jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ModuleStatusHolder state, String name) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("Status", true);
                jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ModuleNameHelper.write ($out, name);
                $in = _invoke ($out);
                state.value = jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ModuleStatusHelper.read ($in);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:ToolboxAPI/APIError:1.0"))
                    throw jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIErrorHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                Status (state, name        );
            } finally {
                _releaseReply ($in);
            }
  } // Status


  //
  public short SuperClasses (jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ClassListHolder classes, String name) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("SuperClasses", true);
                jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ClassNameHelper.write ($out, name);
                $in = _invoke ($out);
                short $result = $in.read_ushort ();
                classes.value = jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ClassListHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:ToolboxAPI/APIError:1.0"))
                    throw jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIErrorHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return SuperClasses (classes, name        );
            } finally {
                _releaseReply ($in);
            }
  } // SuperClasses

  public short SubClasses (jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ClassListHolder classes, String name) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("SubClasses", true);
                jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ClassNameHelper.write ($out, name);
                $in = _invoke ($out);
                short $result = $in.read_ushort ();
                classes.value = jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ClassListHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:ToolboxAPI/APIError:1.0"))
                    throw jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIErrorHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return SubClasses (classes, name        );
            } finally {
                _releaseReply ($in);
            }
  } // SubClasses


  //    unsigned short Uses(out ClassList classes, in ClassName name)
  public short UsesOf (jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ClassListHolder classes, String name) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("UsesOf", true);
                jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ClassNameHelper.write ($out, name);
                $in = _invoke ($out);
                short $result = $in.read_ushort ();
                classes.value = jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ClassListHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:ToolboxAPI/APIError:1.0"))
                    throw jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIErrorHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return UsesOf (classes, name        );
            } finally {
                _releaseReply ($in);
            }
  } // UsesOf

  public short UsedBy (jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ClassListHolder classes, String name) throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("UsedBy", true);
                jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ClassNameHelper.write ($out, name);
                $in = _invoke ($out);
                short $result = $in.read_ushort ();
                classes.value = jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ClassListHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:ToolboxAPI/APIError:1.0"))
                    throw jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIErrorHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return UsedBy (classes, name        );
            } finally {
                _releaseReply ($in);
            }
  } // UsedBy

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:ToolboxAPI/VDMModuleRepos:1.0"};

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
} // class _VDMModuleReposStub
