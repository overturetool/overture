package jp.co.csk.vdm.toolbox.api.corba.VDM;


/**
* jp/co/csk/vdm/toolbox/api/corba/VDM/_VDMFactoryStub.java .
* IDL-to-Java コンパイラ (ポータブル), バージョン "3.1" で生成
* 生成元: metaiv_idl.idl
* 2009年3月16日 10時22分52秒 JST
*/

public class _VDMFactoryStub extends org.omg.CORBA.portable.ObjectImpl implements jp.co.csk.vdm.toolbox.api.corba.VDM.VDMFactory
{

  public jp.co.csk.vdm.toolbox.api.corba.VDM.VDMNumeric MkNumeric (short id, double d)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("MkNumeric", true);
                jp.co.csk.vdm.toolbox.api.corba.VDM.ClientIDHelper.write ($out, id);
                $out.write_double (d);
                $in = _invoke ($out);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMNumeric $result = jp.co.csk.vdm.toolbox.api.corba.VDM.VDMNumericHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return MkNumeric (id, d        );
            } finally {
                _releaseReply ($in);
            }
  } // MkNumeric

  public jp.co.csk.vdm.toolbox.api.corba.VDM.VDMBool MkBool (short id, boolean b)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("MkBool", true);
                jp.co.csk.vdm.toolbox.api.corba.VDM.ClientIDHelper.write ($out, id);
                $out.write_boolean (b);
                $in = _invoke ($out);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMBool $result = jp.co.csk.vdm.toolbox.api.corba.VDM.VDMBoolHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return MkBool (id, b        );
            } finally {
                _releaseReply ($in);
            }
  } // MkBool

  public jp.co.csk.vdm.toolbox.api.corba.VDM.VDMNil MkNil (short id)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("MkNil", true);
                jp.co.csk.vdm.toolbox.api.corba.VDM.ClientIDHelper.write ($out, id);
                $in = _invoke ($out);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMNil $result = jp.co.csk.vdm.toolbox.api.corba.VDM.VDMNilHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return MkNil (id        );
            } finally {
                _releaseReply ($in);
            }
  } // MkNil

  public jp.co.csk.vdm.toolbox.api.corba.VDM.VDMQuote MkQuote (short id, String s)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("MkQuote", true);
                jp.co.csk.vdm.toolbox.api.corba.VDM.ClientIDHelper.write ($out, id);
                $out.write_string (s);
                $in = _invoke ($out);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMQuote $result = jp.co.csk.vdm.toolbox.api.corba.VDM.VDMQuoteHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return MkQuote (id, s        );
            } finally {
                _releaseReply ($in);
            }
  } // MkQuote

  public jp.co.csk.vdm.toolbox.api.corba.VDM.VDMChar MkChar (short id, char c)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("MkChar", true);
                jp.co.csk.vdm.toolbox.api.corba.VDM.ClientIDHelper.write ($out, id);
                $out.write_char (c);
                $in = _invoke ($out);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMChar $result = jp.co.csk.vdm.toolbox.api.corba.VDM.VDMCharHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return MkChar (id, c        );
            } finally {
                _releaseReply ($in);
            }
  } // MkChar

  public jp.co.csk.vdm.toolbox.api.corba.VDM.VDMText MkText (short id, String s)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("MkText", true);
                jp.co.csk.vdm.toolbox.api.corba.VDM.ClientIDHelper.write ($out, id);
                $out.write_string (s);
                $in = _invoke ($out);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMText $result = jp.co.csk.vdm.toolbox.api.corba.VDM.VDMTextHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return MkText (id, s        );
            } finally {
                _releaseReply ($in);
            }
  } // MkText

  public jp.co.csk.vdm.toolbox.api.corba.VDM.VDMToken MkToken (short id, String s)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("MkToken", true);
                jp.co.csk.vdm.toolbox.api.corba.VDM.ClientIDHelper.write ($out, id);
                $out.write_string (s);
                $in = _invoke ($out);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMToken $result = jp.co.csk.vdm.toolbox.api.corba.VDM.VDMTokenHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return MkToken (id, s        );
            } finally {
                _releaseReply ($in);
            }
  } // MkToken

  public jp.co.csk.vdm.toolbox.api.corba.VDM.VDMMap MkMap (short id)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("MkMap", true);
                jp.co.csk.vdm.toolbox.api.corba.VDM.ClientIDHelper.write ($out, id);
                $in = _invoke ($out);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMMap $result = jp.co.csk.vdm.toolbox.api.corba.VDM.VDMMapHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return MkMap (id        );
            } finally {
                _releaseReply ($in);
            }
  } // MkMap

  public jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSequence MkSequence (short id)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("MkSequence", true);
                jp.co.csk.vdm.toolbox.api.corba.VDM.ClientIDHelper.write ($out, id);
                $in = _invoke ($out);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSequence $result = jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSequenceHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return MkSequence (id        );
            } finally {
                _releaseReply ($in);
            }
  } // MkSequence

  public jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSet MkSet (short id)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("MkSet", true);
                jp.co.csk.vdm.toolbox.api.corba.VDM.ClientIDHelper.write ($out, id);
                $in = _invoke ($out);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSet $result = jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSetHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return MkSet (id        );
            } finally {
                _releaseReply ($in);
            }
  } // MkSet


  //    raises (ToolboxAPI::APIError);
  public jp.co.csk.vdm.toolbox.api.corba.VDM.VDMTuple MkTuple (short id, int length)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("MkTuple", true);
                jp.co.csk.vdm.toolbox.api.corba.VDM.ClientIDHelper.write ($out, id);
                $out.write_ulong (length);
                $in = _invoke ($out);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMTuple $result = jp.co.csk.vdm.toolbox.api.corba.VDM.VDMTupleHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return MkTuple (id, length        );
            } finally {
                _releaseReply ($in);
            }
  } // MkTuple

  public jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric FromCPPValue (short id, byte[] cppvalue)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("FromCPPValue", true);
                jp.co.csk.vdm.toolbox.api.corba.VDM.ClientIDHelper.write ($out, id);
                jp.co.csk.vdm.toolbox.api.corba.VDM.bytesHelper.write ($out, cppvalue);
                $in = _invoke ($out);
                jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric $result = jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGenericHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return FromCPPValue (id, cppvalue        );
            } finally {
                _releaseReply ($in);
            }
  } // FromCPPValue

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:VDM/VDMFactory:1.0"};

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
} // class _VDMFactoryStub
