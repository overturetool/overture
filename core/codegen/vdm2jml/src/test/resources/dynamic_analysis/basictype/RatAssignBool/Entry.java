package project;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class Entry {
    /*@ public ghost static boolean invChecksOn = true; @*/
    private Entry() {
    }

    public static Object Run() {
        Number i = 123.456;
        //@ assert Utils.is_rat(i);
        IO.println("Before valid use.");
        i = i.doubleValue() * i.doubleValue();
        //@ assert Utils.is_rat(i);
        IO.println("After valid use.");
        IO.println("Before invalid use.");
        i = ratOpt();
        //@ assert Utils.is_rat(i);
        IO.println("After invalid use.");

        return 0L;
    }

    /*@ pure @*/
    public static Number ratOpt() {
        Number ret_1 = null;

        //@ assert ((ret_1 == null) || Utils.is_rat(ret_1));
        return ret_1;
    }

    public String toString() {
        return "Entry{}";
    }
}
