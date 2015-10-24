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
        IO.println("Before valid use");

        {
            Number e = 2L;
            //@ assert ((e == null) || Utils.is_nat(e) && inv_Entry_Even(e));
            e = null;

            //@ assert ((e == null) || Utils.is_nat(e) && inv_Entry_Even(e));
        }

        IO.println("After valid use");
        IO.println("Before invalid use");

        {
            Number e = 2L;
            //@ assert (Utils.is_nat(e) && inv_Entry_Even(e));
            e = Nil();

            //@ assert (Utils.is_nat(e) && inv_Entry_Even(e));
        }

        IO.println("After invalid use");

        return 0L;
    }

    /*@ pure @*/
    public static Number Nil() {
        Number ret_1 = null;

        //@ assert ((ret_1 == null) || Utils.is_nat(ret_1) && inv_Entry_Even(ret_1));
        return ret_1;
    }

    public String toString() {
        return "Entry{}";
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_Even(final Object check_e) {
        Number e = ((Number) check_e);

        return Utils.equals(Utils.mod(e.longValue(), 2L), 0L);
    }
}
