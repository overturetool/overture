package project;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class Entry {
    /*@ spec_public @*/
    private static project.Entrytypes.St St = new project.Entrytypes.St(5L);

    /*@ public ghost static boolean invChecksOn = true; @*/
    private Entry() {
    }

    public static Object Run() {
        opAtomic();
        IO.println("Before breaking state invariant");
        op();
        IO.println("After breaking state invariant");

        return St.get_x();
    }

    public static void opAtomic() {
        Number atomicTmp_1 = -1L;

        Number atomicTmp_2 = 1L;
        //@ set invChecksOn = false;
        { /* Start of atomic statement */
            St.set_x(atomicTmp_1);
            St.set_x(atomicTmp_2);
        } /* End of atomic statement */
        //@ set invChecksOn = true;

        //@ assert St.valid();
    }

    public static void op() {
        St.set_x(-10L);
        St.set_x(10L);
    }

    public String toString() {
        return "Entry{" + "St := " + Utils.toString(St) + "}";
    }
}
