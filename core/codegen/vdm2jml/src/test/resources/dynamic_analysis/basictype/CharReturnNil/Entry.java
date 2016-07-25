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

    IO.println("Before valid use.");
    {
      final Character ignorePattern_1 = charA();
      //@ assert Utils.is_char(ignorePattern_1);

      /* skip */
    }

    IO.println("After valid use.");
    IO.println("Before invalid use.");
    {
      final Character ignorePattern_2 = charNil();
      //@ assert Utils.is_char(ignorePattern_2);

      /* skip */
    }

    IO.println("After invalid use.");
    return 0L;
  }
  /*@ pure @*/

  public static Character charA() {

    Character ret_1 = 'a';
    //@ assert ((ret_1 == null) || Utils.is_char(ret_1));

    return ret_1;
  }
  /*@ pure @*/

  public static Character charNil() {

    Character ret_2 = null;
    //@ assert ((ret_2 == null) || Utils.is_char(ret_2));

    return ret_2;
  }

  public String toString() {

    return "Entry{}";
  }
}
