package jp.co.csk.vdm.toolbox.api.corba.VDM;


/**
* jp/co/csk/vdm/toolbox/api/corba/VDM/_VDMMapStub.java .
* IDL-to-Java コンパイラ (ポータブル), バージョン "3.1" で生成
* 生成元: metaiv_idl.idl
* 2009年3月16日 10時22分52秒 JST
*/

public class _VDMMapStub extends org.omg.CORBA.portable.ObjectImpl implements jp.co.csk.vdm.toolbox.api.corba.VDM.VDMMap
{

  public void Insert (jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric key, jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric val) throws jp.co.csk.vdm.toolbox.api.corba.VDM.VDMError
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("Insert", true);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGenericHelper.write ($out, key);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGenericHelper.write ($out, val);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:VDM/VDMError:1.0"))
                    throw jp.co.csk.vdm.toolbox.api.corba.VDM.VDMErrorHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                Insert (key, val        );
            } finally {
                _releaseReply ($in);
            }
  } // Insert

  public void ImpModify (jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric key, jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric val)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("ImpModify", true);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGenericHelper.write ($out, key);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGenericHelper.write ($out, val);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                ImpModify (key, val        );
            } finally {
                _releaseReply ($in);
            }
  } // ImpModify

  public jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric Apply (jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric key) throws jp.co.csk.vdm.toolbox.api.corba.VDM.VDMError
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("Apply", true);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGenericHelper.write ($out, key);
                $in = _invoke ($out);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric $result = jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGenericHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:VDM/VDMError:1.0"))
                    throw jp.co.csk.vdm.toolbox.api.corba.VDM.VDMErrorHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return Apply (key        );
            } finally {
                _releaseReply ($in);
            }
  } // Apply

  public void ImpOverride (jp.co.csk.vdm.toolbox.api.corba.VDM.VDMMap m)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("ImpOverride", true);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMMapHelper.write ($out, m);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                ImpOverride (m        );
            } finally {
                _releaseReply ($in);
            }
  } // ImpOverride

  public int Size ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("Size", true);
                $in = _invoke ($out);
                int $result = $in.read_ulong ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return Size (        );
            } finally {
                _releaseReply ($in);
            }
  } // Size

  public boolean IsEmpty ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("IsEmpty", true);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return IsEmpty (        );
            } finally {
                _releaseReply ($in);
            }
  } // IsEmpty

  public jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSet Dom ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("Dom", true);
                $in = _invoke ($out);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSet $result = jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSetHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return Dom (        );
            } finally {
                _releaseReply ($in);
            }
  } // Dom

  public jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSet Rng ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("Rng", true);
                $in = _invoke ($out);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSet $result = jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSetHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return Rng (        );
            } finally {
                _releaseReply ($in);
            }
  } // Rng

  public boolean DomExists (jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric g)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("DomExists", true);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGenericHelper.write ($out, g);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return DomExists (g        );
            } finally {
                _releaseReply ($in);
            }
  } // DomExists

  public void RemElem (jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric g) throws jp.co.csk.vdm.toolbox.api.corba.VDM.VDMError
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("RemElem", true);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGenericHelper.write ($out, g);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:VDM/VDMError:1.0"))
                    throw jp.co.csk.vdm.toolbox.api.corba.VDM.VDMErrorHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                RemElem (g        );
            } finally {
                _releaseReply ($in);
            }
  } // RemElem

  public short First (jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGenericHolder g)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("First", true);
                $in = _invoke ($out);
                short $result = $in.read_short ();
                g.value = jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGenericHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return First (g        );
            } finally {
                _releaseReply ($in);
            }
  } // First

  public short Next (jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGenericHolder g)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("Next", true);
                $in = _invoke ($out);
                short $result = $in.read_short ();
                g.value = jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGenericHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return Next (g        );
            } finally {
                _releaseReply ($in);
            }
  } // Next

  public String ToAscii ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("ToAscii", true);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return ToAscii (        );
            } finally {
                _releaseReply ($in);
            }
  } // ToAscii

  public boolean IsNil ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("IsNil", true);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return IsNil (        );
            } finally {
                _releaseReply ($in);
            }
  } // IsNil

  public boolean IsChar ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("IsChar", true);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return IsChar (        );
            } finally {
                _releaseReply ($in);
            }
  } // IsChar

  public boolean IsNumeric ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("IsNumeric", true);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return IsNumeric (        );
            } finally {
                _releaseReply ($in);
            }
  } // IsNumeric

  public boolean IsQuote ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("IsQuote", true);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return IsQuote (        );
            } finally {
                _releaseReply ($in);
            }
  } // IsQuote

  public boolean IsTuple ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("IsTuple", true);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return IsTuple (        );
            } finally {
                _releaseReply ($in);
            }
  } // IsTuple

  public boolean IsRecord ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("IsRecord", true);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return IsRecord (        );
            } finally {
                _releaseReply ($in);
            }
  } // IsRecord

  public boolean IsSet ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("IsSet", true);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return IsSet (        );
            } finally {
                _releaseReply ($in);
            }
  } // IsSet

  public boolean IsMap ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("IsMap", true);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return IsMap (        );
            } finally {
                _releaseReply ($in);
            }
  } // IsMap

  public boolean IsText ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("IsText", true);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return IsText (        );
            } finally {
                _releaseReply ($in);
            }
  } // IsText

  public boolean IsToken ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("IsToken", true);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return IsToken (        );
            } finally {
                _releaseReply ($in);
            }
  } // IsToken

  public boolean IsBool ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("IsBool", true);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return IsBool (        );
            } finally {
                _releaseReply ($in);
            }
  } // IsBool

  public boolean IsSequence ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("IsSequence", true);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return IsSequence (        );
            } finally {
                _releaseReply ($in);
            }
  } // IsSequence

  public boolean IsObjectRef ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("IsObjectRef", true);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return IsObjectRef (        );
            } finally {
                _releaseReply ($in);
            }
  } // IsObjectRef

  public void Destroy () throws jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("Destroy", true);
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
                Destroy (        );
            } finally {
                _releaseReply ($in);
            }
  } // Destroy


  // be released.
  public byte[] GetCPPValue ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("GetCPPValue", true);
                $in = _invoke ($out);
                byte $result[] = jp.co.csk.vdm.toolbox.api.corba.VDM.bytesHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return GetCPPValue (        );
            } finally {
                _releaseReply ($in);
            }
  } // GetCPPValue


  // iterating through a large VDM value.
  public jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric Clone ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("Clone", true);
                $in = _invoke ($out);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric $result = jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGenericHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return Clone (        );
            } finally {
                _releaseReply ($in);
            }
  } // Clone

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:VDM/VDMMap:1.0", 
    "IDL:VDM/VDMGeneric:1.0"};

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
} // class _VDMMapStub
