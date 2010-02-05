// ***** VDMTOOLS START Name=HeaderComment KEEP=YES
// Implementation of the VDM standard IO library
// Use this file instead of the automatically generated IO.java file
// $Revision: 1.2 $
// $Date: 2005/05/11 06:45:49 $

// ***** VDMTOOLS END Name=HeaderComment

// ***** VDMTOOLS START Name=package KEEP=YES
package jp.co.csk.vdm.toolbox.VDM.stdlib;
// ***** VDMTOOLS END Name=package


// ***** VDMTOOLS START Name=imports KEEP=YES

import jp.co.csk.vdm.toolbox.VDM.*;
import jp.co.csk.vdm.toolbox.VDM.quotes.append;
import jp.co.csk.vdm.toolbox.VDM.quotes.start;
import java.io.*;
// ***** VDMTOOLS END Name=imports



public class IO implements EvaluatePP {

// ***** VDMTOOLS START Name=vdmComp KEEP=YES
  static UTIL.VDMCompare vdmComp = new UTIL.VDMCompare();
// ***** VDMTOOLS END Name=vdmComp

// ***** VDMTOOLS START Name=sentinel KEEP=YES
  volatile Sentinel sentinel;
// ***** VDMTOOLS END Name=sentinel


// ***** VDMTOOLS START Name=IOSentinel KEEP=YES
  class IOSentinel extends Sentinel {

    public final int echo = 0;
    public final int fecho = 1;
    public final int ferror = 2;
    public final int nr_functions = 3;

    public IOSentinel () throws CGException{}

    public IOSentinel (EvaluatePP instance) throws CGException{
      init(nr_functions, instance);
    }

  }
// ***** VDMTOOLS END Name=IOSentinel
;

// ***** VDMTOOLS START Name=evaluatePP KEEP=YES
  public Boolean evaluatePP (int fnr) throws CGException{
    return new Boolean(true);
  }
// ***** VDMTOOLS END Name=evaluatePP


// ***** VDMTOOLS START Name=setSentinel KEEP=YES
  public void setSentinel () {
    try{
      sentinel = new IOSentinel(this);
    }
    catch (CGException e) {
      System.out.println(e.getMessage());
    }
  }
// ***** VDMTOOLS END Name=setSentinel


// ***** VDMTOOLS START Name=error KEEP=YES
  // Variable to store potential error message that later can be
  // retrieved by ferror
  private String error = "";
// ***** VDMTOOLS END Name=error
    
// ***** VDMTOOLS START Name=IO KEEP=YES
  public IO () {
    try{
      setSentinel();
    }
    catch (Throwable e) {
      System.out.println(e.getMessage());
    }
  }
// ***** VDMTOOLS END Name=IO


// ***** VDMTOOLS START Name=writeval KEEP=YES
  public Boolean writeval (final Object val) throws CGException{    
    try {
      System.out.println(UTIL.toString(val));
      return new Boolean(true);
    }
    catch (Exception e) {
      error = e.getMessage();
      return new Boolean(false);
    }
  }
// ***** VDMTOOLS END Name=writeval


// ***** VDMTOOLS START Name=fwriteval KEEP=YES
  public Boolean fwriteval (final String filename, final Object val, 
                            final Object fdir) throws CGException {    
    try {
      FileWriter fw;
      boolean append = false;
      if (fdir instanceof start)
          append = false;
      else if (fdir instanceof append)
          append = true;
      fw = new FileWriter(filename, append);
            
      fw.write(UTIL.toString(val));
      fw.flush();
      fw.close();
      return new Boolean(true);
    }
    catch (Exception e) {
      error = e.getMessage();
      return new Boolean(false);
    }
  }
// ***** VDMTOOLS END Name=fwriteval


// ***** VDMTOOLS START Name=freadval KEEP=YES
  public Tuple freadval (final String f) throws CGException{    
    Tuple result = new Tuple(2);
    try {
      result = jp.co.csk.vdm.toolbox.VDM.ValueParser.ValueParser.ParseVDMValue(f);
    }
    catch (Exception e) {
      try {
        error = e.getMessage();
        result.SetField(1, new Boolean(false));
        result.SetField(2, error);
      }
      catch (Exception ex) {
          // Example of who comes first the egg or the hen ;-)
          // Exceptions thrown from Tuple.SetField are not returned.
      }
    }
    return result;
  }
// ***** VDMTOOLS END Name=freadval


// ***** VDMTOOLS START Name=post_freadval KEEP=YES
  public Boolean post_freadval (final String f, final Tuple RESULT) 
    throws CGException 
  {    
    if (RESULT.Length() != 2)
        UTIL.RunTime("Run-Time Error: Pattern match did" + 
                     " not succeed in value definition");
    Boolean b = (Boolean) RESULT.GetField(1);
    Object t = RESULT.GetField(2);
    if (!b.booleanValue())
        return new Boolean(t == null);
    else
        return new Boolean(true);
  }
// ***** VDMTOOLS END Name=post_freadval


// ***** VDMTOOLS START Name=echo KEEP=YES
  public Boolean echo (final String text) throws CGException {
    sentinel.entering(((IOSentinel) sentinel).echo);
    try {
      try {
        System.out.println(text);
        return new Boolean(true);
      }
      catch (Exception e) {
        error = e.getMessage();
        return new Boolean(false);
      }
    }
    finally {
      sentinel.leaving(((IOSentinel) sentinel).echo);
    }
  }
// ***** VDMTOOLS END Name=echo


// ***** VDMTOOLS START Name=fecho KEEP=YES
  public Boolean fecho (final String filename, final String text, 
                        final Object fdir) throws CGException 
  {
    sentinel.entering(((IOSentinel) sentinel).fecho);
    try {
      try {
        FileWriter fw;
        boolean append = false;
        if (fdir instanceof start)
          append = false;
        else if (fdir instanceof append)
          append = true;
        fw = new FileWriter(filename, append);
            
        fw.write(text);
        fw.flush();
        fw.close();
        return new Boolean(true);
      }
      catch (Exception e) {
        error = e.getMessage();
        return new Boolean(false);
      }
    }
    finally {
      sentinel.leaving(((IOSentinel) sentinel).fecho);
    }
  }
// ***** VDMTOOLS END Name=fecho


// ***** VDMTOOLS START Name=pre_fecho KEEP=YES
  public Boolean pre_fecho (final String filename, final String text, 
                            final Object fdir) throws CGException 
  {
    if (filename.equals(""))
      return new Boolean(fdir == null);
    else  
      return new Boolean(fdir != null);  
  }
// ***** VDMTOOLS END Name=pre_fecho


// ***** VDMTOOLS START Name=ferror KEEP=YES
  public String ferror () throws CGException{
    sentinel.entering(((IOSentinel) sentinel).ferror);
    try {
      String str = error;
      error = "";
      return str;
    }
    finally {
      sentinel.leaving(((IOSentinel) sentinel).ferror);
    }
  }
// ***** VDMTOOLS END Name=ferror

}
;
