

package org.overturetool.VDM2JavaCG.api;


import jp.co.csk.vdm.toolbox.VDM.*;
import java.util.*;
import org.overturetool.api.io.*;
import org.overturetool.api.io.*;
import org.overturetool.api.*;
import jp.co.csk.vdm.toolbox.VDM.quotes.*;
import org.overturetool.VDM2JavaCG.api.*;



@SuppressWarnings({"all","unchecked","unused"})
public class Util extends StdLib {

// ***** VDMTOOLS START Name=vdmComp KEEP=NO
  static UTIL.VDMCompare vdmComp = new UTIL.VDMCompare();
// ***** VDMTOOLS END Name=vdmComp

// ***** VDMTOOLS START Name=writeType KEEP=YES
  private static Object writeType = new start();
// ***** VDMTOOLS END Name=writeType

// ***** VDMTOOLS START Name=outputFileName KEEP=NO
  public static String outputFileName = new String("tmp.xmi");
// ***** VDMTOOLS END Name=outputFileName


// ***** VDMTOOLS START Name=vdm_init_Util KEEP=NO
  private void vdm_init_Util () throws CGException {}
// ***** VDMTOOLS END Name=vdm_init_Util


// ***** VDMTOOLS START Name=Util KEEP=NO
  public Util () throws CGException {
    vdm_init_Util();
  }
// ***** VDMTOOLS END Name=Util


// ***** VDMTOOLS START Name=DebugPrint#1|String KEEP=NO
  static public void DebugPrint (final String debugString) throws CGException {

    IOProxy file = (IOProxy) new IOProxy();
    file.print(debugString);
  }
// ***** VDMTOOLS END Name=DebugPrint#1|String


// ***** VDMTOOLS START Name=CreateFile#1|String KEEP=YES
  static public void CreateFile (final String fName) throws CGException {

  file.CreateFile(fName);
  writeType = new append();
  }
// ***** VDMTOOLS END Name=CreateFile#1|String


// ***** VDMTOOLS START Name=CloseFile KEEP=YES
  static public void CloseFile () throws CGException {
    file.CloseFile();
  }
// ***** VDMTOOLS END Name=CloseFile


// ***** VDMTOOLS START Name=WriteFile#1|String KEEP=YES
 static BufferedFileWriter file = new BufferedFileWriter();
  static public void WriteFile (final String data) throws CGException {

   boolean append =!(writeType.toString().equals("<start>"));
   file.write(data, append);
  }
// ***** VDMTOOLS END Name=WriteFile#1|String

}
;