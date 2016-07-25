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

    Number n = 1L;
    //@ assert Utils.is_nat1(n);

    IO.println("Before valid use.");
    n = 1L;
    //@ assert Utils.is_nat1(n);

    IO.println("After valid use.");
    IO.println("Before invalid use.");
    {
      Number n1 = -1L + 1L;
      //@ assert Utils.is_nat1(n1);

      /* skip */
    }

    IO.println("After invalid use.");
    return 0L;
  }

  public String toString() {

    return "Entry{}";
  }
}
