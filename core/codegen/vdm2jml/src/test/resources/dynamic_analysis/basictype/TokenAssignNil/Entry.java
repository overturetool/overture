package project;

import java.util.*;
import org.overture.codegen.runtime.*;
import org.overture.codegen.vdm2jml.runtime.*;

@SuppressWarnings("all")
//@ nullable_by_default

final public class Entry {
  //@ public static invariant ((n == null) || Utils.is_token(n));

  public static final Token n = null;
  //@ public static invariant ((t == null) || Utils.is_token(t));

  public static final Token t = new Token("");
  /*@ public ghost static boolean invChecksOn = true; @*/

  private Entry() {}

  public static Object Run() {

    IO.println("Before valid use.");
    {
      final Token ignorePattern_1 = t;
      //@ assert Utils.is_token(ignorePattern_1);

      /* skip */
    }

    IO.println("After valid use.");
    IO.println("Before invalid use.");
    {
      final Token ignorePattern_2 = n;
      //@ assert Utils.is_token(ignorePattern_2);

      /* skip */
    }

    IO.println("After invalid use.");
    return 0L;
  }

  public String toString() {

    return "Entry{" + "n = " + Utils.toString(n) + ", t = " + Utils.toString(t) + "}";
  }
}
