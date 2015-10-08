package project;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class Entry {
    /*@ spec_public @*/
    private static project.Entrytypes.St St = new project.Entrytypes.St(1L);

    /*@ public ghost static boolean invChecksOn = true; @*/
    private Entry() {
    }

    public static Object Run() {
        IO.println("Before first atomic (expecting violation after atomic)");

        Number atomicTmp_1 = 2L;
        //@ assert Utils.is_nat(atomicTmp_1);
        { /* Start of atomic statement */
            //@ set invChecksOn = false;

            //@ assert St != null;
            St.set_x(atomicTmp_1);

            //@ set invChecksOn = true;

            //@ assert St.valid();
        } /* End of atomic statement */
        IO.println(
            "After first atomic (expected violation before this print statement)");
        IO.println("Before second atomic");

        Number atomicTmp_2 = 1L;
        //@ assert Utils.is_nat(atomicTmp_2);
        { /* Start of atomic statement */
            //@ set invChecksOn = false;

            //@ assert St != null;
            St.set_x(atomicTmp_2);

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
