package project;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
public class Entry {
    /*@ spec_public @*/
    private static project.Entrytypes.St St = new project.Entrytypes.St(5L);

    //@ public static invariant St != null ==> inv_St(St);
    public static Object Run() {
        opAtomic();
        IO.println("Before breaking state invariant");
        op();
        IO.println("After breaking state invariant");

        return St.x;
    }

    public static void opAtomic() {
        St.x = -1L;
        St.x = 1L;
    }

    public static void op() {
        St.x = -10L;
        //@ assert inv_St(St);
        St.x = 10L;

        //@ assert inv_St(St);
    }

    public String toString() {
        return "Entry{" + "St := " + Utils.toString(St) + "}";
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_St(final project.Entrytypes.St s) {
        return s.x.longValue() > 0L;
    }
}
