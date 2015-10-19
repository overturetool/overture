package project;

import org.overture.codegen.runtime.*;
import org.overture.codegen.vdm2jml.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class Entry {
    /*@ public ghost static boolean invChecksOn = true; @*/
    private Entry() {
    }

    public static Object Run() {
        Number n = 0L;
        //@ assert ((n == null) || Utils.is_nat(n));
        IO.println("Before valid use.");
        n = 1L;
        //@ assert ((n == null) || Utils.is_nat(n));
        n = null;
        //@ assert ((n == null) || Utils.is_nat(n));
        IO.println("After valid use.");
        IO.println("Before invalid use.");
        n = idNat(n);
        //@ assert ((n == null) || Utils.is_nat(n));
        IO.println("After invalid use.");

        return 0L;
    }

    /*@ pure @*/
    public static Number idNat(final Number x) {
        //@ assert Utils.is_nat(x);
        Number ret_1 = x;

        //@ assert Utils.is_nat(ret_1);
        return ret_1;
    }

    public String toString() {
        return "Entry{}";
    }
}
