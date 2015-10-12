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
        Boolean b = false;
        //@ assert Utils.is_bool(b);
        IO.println("Before valid use.");
        b = true;
        //@ assert Utils.is_bool(b);
        IO.println("After valid use.");
        IO.println("Before invalid use.");
        b = boolNil();
        //@ assert Utils.is_bool(b);
        IO.println("After invalid use.");

        return 0L;
    }

    /*@ pure @*/
    public static Boolean boolTrue() {
        Boolean ret_1 = true;

        //@ assert Utils.is_bool(ret_1);
        return ret_1;
    }

    /*@ pure @*/
    public static Boolean boolNil() {
        Boolean ret_2 = null;

        //@ assert ((ret_2 == null) || Utils.is_bool(ret_2));
        return ret_2;
    }

    public String toString() {
        return "Entry{}";
    }
}
