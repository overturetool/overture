package project;

import java.util.*;
import org.overture.codegen.runtime.*;
import org.overture.codegen.vdm2jml.runtime.*;

@SuppressWarnings("all")
//@ nullable_by_default

final public class Entry {
  /*@ spec_public @*/

  private static project.Entrytypes.St St = new project.Entrytypes.St(1L);
  /*@ public ghost static boolean invChecksOn = true; @*/

  private Entry() {}

  public static Object Run() {

    IO.println("Before first atomic (expecting violation after atomic)");
    Number atomicTmp_1 = 2L;
    //@ assert Utils.is_nat(atomicTmp_1);

    Number atomicTmp_2 = 2L;
    //@ assert Utils.is_nat(atomicTmp_2);

    {
        /* Start of atomic statement */
      //@ set invChecksOn = false;

      //@ assert St != null;

      St.set_x(atomicTmp_1);

      //@ assert St != null;

      St.set_x(atomicTmp_2);

      //@ set invChecksOn = true;

      //@ assert St.valid();

    } /* End of atomic statement */

    IO.println("After first atomic (expected violation before this print statement)");
    IO.println("Before second atomic");
    Number atomicTmp_3 = 1L;
    //@ assert Utils.is_nat(atomicTmp_3);

    Number atomicTmp_4 = 1L;
    //@ assert Utils.is_nat(atomicTmp_4);

    {
        /* Start of atomic statement */
      //@ set invChecksOn = false;

      //@ assert St != null;

      St.set_x(atomicTmp_3);

      //@ assert St != null;

      St.set_x(atomicTmp_4);

      //@ set invChecksOn = true;

      //@ assert St.valid();

    } /* End of atomic statement */

    IO.println("After second atomic");
    return 2L;
  }

  public String toString() {

    return "Entry{" + "St := " + Utils.toString(St) + "}";
  }
}
