package project;

import java.util.*;
import org.overture.codegen.runtime.*;
import org.overture.codegen.vdm2jml.runtime.*;

@SuppressWarnings("all")
//@ nullable_by_default

final public class Entry {
  /*@ public ghost static boolean invChecksOn = true; @*/

  private Entry() {}

  public static Object Run() {

    final project.quotes.AQuote aOpt = null;
    //@ assert ((aOpt == null) || Utils.is_(aOpt,project.quotes.AQuote.class));

    final project.quotes.AQuote a = project.quotes.AQuote.getInstance();
    //@ assert Utils.is_(a,project.quotes.AQuote.class);

    {
      IO.println("Before passing LEGAL value");
      op(a);
      IO.println("After passing LEGAL value");
      IO.println("Before passing ILLEGAL value");
      op(aOpt);
      IO.println("After passing ILLEGAL value");
      return true;
    }
  }

  public static void op(final project.quotes.AQuote ignorePattern_1) {

    //@ assert Utils.is_(ignorePattern_1,project.quotes.AQuote.class);

    /* skip */

  }

  public String toString() {

    return "Entry{}";
  }
}
