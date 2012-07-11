package org.overturetool.VDM2JavaCG.main;


import jp.co.csk.vdm.toolbox.VDM.*;

import java.io.PrintWriter;
import java.util.*;
import org.overturetool.vdmj.definitions.ValueDefinition;
import org.overturetool.vdmj.types.Type;


@SuppressWarnings({"all","unchecked","unused"})
public class StatusLog {

  private Vector log = null;
  public static PrintWriter out=new PrintWriter(System.out);

  private void vdm_init_StatusLog () throws CGException {
    try {
      log = new Vector();
    }
    catch (Exception e){

      e.printStackTrace(System.out);
      System.out.println(e.getMessage());
    }
  }

  public StatusLog () throws CGException {
    vdm_init_StatusLog();
  }

  public void info (final String message) throws CGException {
	  System.out.println("[INFO] "+message);
  }

  public static void warn (final String message) throws CGException {
	  System.out.println("[WARNING] "+message);
  }

  public void addNewClassInfo (final String name) throws CGException {
    log.add(new ClassStatus(name, new String(""), getTime(), new Long(0)));
  }

  public void addNewClassInfo (final String name, final String i) throws CGException {
    log.add(new ClassStatus(name, i, getTime(), new Long(0)));
  }

  public void endClass (final String name, final String id) throws CGException {

    HashSet a = new HashSet();
    HashSet res_s_5 = new HashSet();
    {

      HashSet e1_set_15 = new HashSet(log);
      ClassStatus c = null;
      {
        for (Iterator enm_17 = e1_set_15.iterator(); enm_17.hasNext(); ) {

          ClassStatus elem_16 = (ClassStatus) enm_17.next();
          c = (ClassStatus) elem_16;
          Boolean pred_9 = null;
          String var1_10 = null;
          var1_10 = c.name;
          pred_9 = new Boolean(UTIL.equals(var1_10, name));
          if (pred_9.booleanValue()) {
            res_s_5.add(setdd((ClassStatus) c, id));
          }
        }
      }
    }
    a = res_s_5;
  }

  public void endClass (final String name) throws CGException {

    HashSet a = new HashSet();
    HashSet res_s_4 = new HashSet();
    {

      HashSet e1_set_16 = new HashSet(log);
      ClassStatus c = null;
      {
        for (Iterator enm_18 = e1_set_16.iterator(); enm_18.hasNext(); ) {

          ClassStatus elem_17 = (ClassStatus) enm_18.next();
          c = (ClassStatus) elem_17;
          Boolean pred_10 = null;
          String var1_11 = null;
          var1_11 = c.name;
          pred_10 = new Boolean(UTIL.equals(var1_11, name));
          if (pred_10.booleanValue()) {

            Long res_s_5 = null;
            res_s_5 = UTIL.NumberToLong(setd(c));
            res_s_4.add(res_s_5);
          }
        }
      }
    }
    a = res_s_4;
  }

  private Long setdd (final ClassStatus node, final String id) throws CGException {

    node.setId(id);
    node.setEndTime(getTime());
    return new Long(0);
  }

  private Long setd (final ClassStatus node, final String id) throws CGException {

    node.setId(id);
    node.setEndTime(getTime());
    return new Long(0);
  }

  private Long setd (final ClassStatus node) throws CGException {

    node.setEndTime(getTime());
    return new Long(0);
  }

  private Long getTime () throws CGException {
    return new Long(0);
  }

  public void mappingNotSupported (final ValueDefinition item) throws CGException {}

  public void mappingNotSupported (final Vector items, final Type type) throws CGException {}

}