package project;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class Entry {
    /*@ spec_public @*/
    private static project.Entrytypes.St St = new project.Entrytypes.St(5L);

    //@ public static invariant St != null ==> inv_St(St);
    private Entry() {
    }

    public static Object Run() {
        opAtomic();
        IO.println("Before breaking state invariant");
        op();
        IO.println("After breaking state invariant");

        return St.x;
    }

    public static void opAtomic() {
        Number atomicTmp_1 = -1L;

        Number atomicTmp_2 = 1L;
        St.x = atomicTmp_1;
        St.x = atomicTmp_2;
    }

    public static void op() {
        St.x = -10L;
        //@ assert inv_St(St);
        St.x = 10L;
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
