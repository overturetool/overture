package jp.co.csk.vdm.toolbox.api.corba.VDM;


/**
* jp/co/csk/vdm/toolbox/api/corba/VDM/_VDMSetStub.java .
* IDL-to-Java コンパイラ (ポータブル), バージョン "3.1" で生成
* 生成元: metaiv_idl.idl
* 2008年4月21日 15時58分01秒 JST
*/

public class _VDMSetStub extends org.omg.CORBA.portable.ObjectImpl implements jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSet
{

  public void Insert (jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric g)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("Insert", true);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGenericHelper.write ($out, g);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                Insert (g        );
            } finally {
                _releaseReply ($in);
            }
  } // Insert

  public int Card ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("Card", true);
                $in = _invoke ($out);
                int $result = $in.read_ulong ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return Card (        );
            } finally {
                _releaseReply ($in);
            }
  } // Card

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

  public boolean InSet (jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric g)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("InSet", true);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGenericHelper.write ($out, g);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return InSet (g        );
            } finally {
                _releaseReply ($in);
            }
  } // InSet

  public void ImpUnion (jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSet s)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("ImpUnion", true);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSetHelper.write ($out, s);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                ImpUnion (s        );
            } finally {
                _releaseReply ($in);
            }
  } // ImpUnion

  public void ImpIntersect (jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSet s)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("ImpIntersect", true);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSetHelper.write ($out, s);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                ImpIntersect (s        );
            } finally {
                _releaseReply ($in);
            }
  } // ImpIntersect

  public jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric GetElem () throws jp.co.csk.vdm.toolbox.api.corba.VDM.VDMError
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("GetElem", true);
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
                return GetElem (        );
            } finally {
                _releaseReply ($in);
            }
  } // GetElem

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

  public boolean SubSet (jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSet s)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("SubSet", true);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSetHelper.write ($out, s);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return SubSet (s        );
            } finally {
                _releaseReply ($in);
            }
  } // SubSet

  public void ImpDiff (jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSet s)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("ImpDiff", true);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSetHelper.write ($out, s);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                ImpDiff (s        );
            } finally {
                _releaseReply ($in);
            }
  } // ImpDiff

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
    "IDL:VDM/VDMSet:1.0", 
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
} // class _VDMSetStub
