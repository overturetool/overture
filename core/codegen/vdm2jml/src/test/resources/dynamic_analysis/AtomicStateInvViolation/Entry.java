package project;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class Entry {
    /*@ spec_public @*/
    private static project.Entrytypes.St St = new project.Entrytypes.St(1L);

    //@ public static invariant St != null ==> inv_St(St);
    private Entry() {
    }

    public static Object Run() {
        Number atomicTmp_1 = 2L;
        St.x = atomicTmp_1;

        //@ assert inv_St(St);
        Number atomicTmp_2 = 1L;
        St.x = atomicTmp_2;

        //@ assert inv_St(St);
        return 2L;
    }

    public String toString() {
        return "Entry{" + "St := " + Utils.toString(St) + "}";
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_St(final project.Entrytypes.St s) {
        return Utils.equals(s.x, 1L);
    }
}
