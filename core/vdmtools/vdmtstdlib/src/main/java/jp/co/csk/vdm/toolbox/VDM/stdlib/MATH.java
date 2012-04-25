// ***** VDMTOOLS START Name=HeaderComment KEEP=NO
// Implementation of the VDM standard MATH library
// Use this file instead of the automatically generated MATH.java file
// $Revision: 1.2 $
// $Date: 2005/05/11 06:45:49 $

// Note that there is no concurrency code, since all of the methods
// correspond to VDM++ functions, which do not have associated history
// counters. 

// ***** VDMTOOLS END Name=HeaderComment

// ***** VDMTOOLS START Name=package KEEP=YES
package jp.co.csk.vdm.toolbox.VDM.stdlib;
// ***** VDMTOOLS END Name=package


// ***** VDMTOOLS START Name=imports KEEP=YES

import jp.co.csk.vdm.toolbox.VDM.*;

// ***** VDMTOOLS END Name=imports



public class MATH {

// ***** VDMTOOLS START Name=pi KEEP=YES
  public static final Double pi = new Double(java.lang.Math.PI);
  public static final RANDOM rd = new RANDOM();
  public static long rnd_seed = -1;
// ***** VDMTOOLS END Name=pi


// ***** VDMTOOLS START Name=sin KEEP=YES
  public static Double sin (final Number v) throws CGException{    
    return new Double(Math.sin(v.doubleValue()));
  }
// ***** VDMTOOLS END Name=sin


// ***** VDMTOOLS START Name=post_sin KEEP=YES
  public static Boolean post_sin (final Number v, final Double RESULT) 
      throws CGException
  {    
      return new Boolean(Math.abs(RESULT.doubleValue()) <= 1);
  }
// ***** VDMTOOLS END Name=post_sin


// ***** VDMTOOLS START Name=cos KEEP=YES
  public static Double cos (final Number v) throws CGException{    
    return new Double(Math.cos(v.doubleValue()));
  }
// ***** VDMTOOLS END Name=cos


// ***** VDMTOOLS START Name=post_cos KEEP=YES
  public static Boolean post_cos (final Number v, final Double RESULT) 
      throws CGException
  {    
    return new Boolean(Math.abs(RESULT.doubleValue()) <= 1);
  }
// ***** VDMTOOLS END Name=post_cos


// ***** VDMTOOLS START Name=tan KEEP=YES
  public static Double tan (final Number a) throws CGException {    
    return new Double(Math.tan(UTIL.NumberToReal(a).doubleValue()));
  }
// ***** VDMTOOLS END Name=tan


// ***** VDMTOOLS START Name=pre_tan KEEP=YES
  public static Boolean pre_tan (final Number a) throws CGException{
    return new Boolean(cos(a).doubleValue() != 0);
  }
// ***** VDMTOOLS END Name=pre_tan


// ***** VDMTOOLS START Name=cot KEEP=YES
  public static Double cot (final Number a) throws CGException{    
    double ad = UTIL.NumberToReal(a).doubleValue();
    return new Double(Math.cos(ad)/Math.sin(ad));
  }
// ***** VDMTOOLS END Name=cot


// ***** VDMTOOLS START Name=pre_cot KEEP=YES
  public static Boolean pre_cot (final Number a) throws CGException{
    return new Boolean(sin(a).doubleValue() != 0);
  }
// ***** VDMTOOLS END Name=pre_cot


// ***** VDMTOOLS START Name=asin KEEP=YES
  public static Double asin (final Number a) throws CGException{    
    return new Double(Math.asin(UTIL.NumberToReal(a).doubleValue()));
  }
// ***** VDMTOOLS END Name=asin


// ***** VDMTOOLS START Name=pre_asin KEEP=YES
  public static Boolean pre_asin (final Number a) throws CGException{    
    return new Boolean(Math.abs(UTIL.NumberToReal(a).doubleValue()) <= 1);
  }
// ***** VDMTOOLS END Name=pre_asin


// ***** VDMTOOLS START Name=acos KEEP=YES
  public static Double acos (final Number a) throws CGException{    
    return new Double(Math.acos(UTIL.NumberToReal(a).doubleValue()));
  }
// ***** VDMTOOLS END Name=acos


// ***** VDMTOOLS START Name=pre_acos KEEP=YES
  public static Boolean pre_acos (final Number a) throws CGException{    
    return new Boolean(Math.abs(UTIL.NumberToReal(a).doubleValue()) <= 1);
  }
// ***** VDMTOOLS END Name=pre_acos


// ***** VDMTOOLS START Name=atan KEEP=YES
  public static Double atan (final Number v) throws CGException{    
    return new Double(Math.atan(v.doubleValue()));
  }
// ***** VDMTOOLS END Name=atan


// ***** VDMTOOLS START Name=acot KEEP=YES
  public static Double acot (final Number a) throws CGException{
    return atan(new Double(new Integer(1).doubleValue() / UTIL.NumberToReal(a).doubleValue()));
  }
// ***** VDMTOOLS END Name=acot


// ***** VDMTOOLS START Name=pre_acot KEEP=YES
  public static Boolean pre_acot (final Number a) throws CGException{
    return new Boolean(UTIL.NumberToReal(a).doubleValue() != 0);
  }
// ***** VDMTOOLS END Name=pre_acot


// ***** VDMTOOLS START Name=sqrt KEEP=YES
  public static Double sqrt (final Number a) throws CGException{    
      return new Double(Math.sqrt(UTIL.NumberToReal(a).doubleValue()));
  }
// ***** VDMTOOLS END Name=sqrt


// ***** VDMTOOLS START Name=pre_sqrt KEEP=YES
  public static Boolean pre_sqrt (final Number a) throws CGException{
    return new Boolean((UTIL.NumberToReal(a).doubleValue()) >= 0);
  }
// ***** VDMTOOLS END Name=pre_sqrt


// ***** VDMTOOLS START Name=srand KEEP=YES
  public static void srand (final Number a) throws CGException{    
    MATH.rnd_seed = UTIL.NumberToInt(a).longValue(); 
    if( MATH.rnd_seed >= 0 )
    {
      rd.set_seed(UTIL.NumberToInt(a).longValue());
    }
  }
// ***** VDMTOOLS END Name=srand


// ***** VDMTOOLS START Name=pre_srand KEEP=YES
  public static Boolean pre_srand (final Number a) throws CGException{
    return new Boolean((UTIL.NumberToInt(a).longValue()) >= -1);
  }
// ***** VDMTOOLS END Name=pre_srand


// ***** VDMTOOLS START Name=rand KEEP=YES
  public static Integer rand (final Number a) throws CGException
  {    
    if( MATH.rnd_seed >= 0 )
    {
      return new Integer(rd.get_random(UTIL.NumberToInt(a).intValue()));
    }
    else
    {
      return new Integer(UTIL.NumberToInt(a).intValue());
    }
  }
// ***** VDMTOOLS END Name=rand


// ***** VDMTOOLS START Name=exp KEEP=YES
  public static Double exp (final Number a) throws CGException{    
    return new Double(Math.exp(UTIL.NumberToReal(a).doubleValue()));
  }
// ***** VDMTOOLS END Name=exp


// ***** VDMTOOLS START Name=ln KEEP=YES
  public static Double ln (final Number a) throws CGException{    
    return new Double(Math.log(UTIL.NumberToReal(a).doubleValue()));
  }
// ***** VDMTOOLS END Name=ln


// ***** VDMTOOLS START Name=pre_ln KEEP=YES
  public static Boolean pre_ln (final Number a) throws CGException{
    return new Boolean((UTIL.NumberToReal(a).doubleValue()) > 0);
  }
// ***** VDMTOOLS END Name=pre_ln


// ***** VDMTOOLS START Name=log KEEP=YES
  public static Double log (final Number a) throws CGException{    
  //    return new Double(Math.log10(UTIL.NumberToReal(a).doubleValue()));
    return new Double(Math.log(UTIL.NumberToReal(a).doubleValue())/Math.log(10));
  }
// ***** VDMTOOLS END Name=log


// ***** VDMTOOLS START Name=pre_log KEEP=YES
  public static Boolean pre_log (final Number a) throws CGException{
    return new Boolean((UTIL.NumberToReal(a).doubleValue()) > 0);
  }
// ***** VDMTOOLS END Name=pre_log


// ***** VDMTOOLS START Name=pi_uf KEEP=YES
  public static Double pi_uf () throws CGException{    
    return pi;
  }
// ***** VDMTOOLS END Name=pi_uf

}
;
