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
        IO.println("Before VALID initialisation");

        {
            Number bOkay = natOne();

            //@ assert Utils.is_nat(bOkay);

            /* skip */
        }

        IO.println("After VALID initialisation");
        IO.println("Before INVALID initialisation");

        {
            Number bError = natNil();

            //@ assert Utils.is_nat(bError);

            /* skip */
        }

        IO.println("After INVALID initialisation");

        return true;
    }

    /*@ pure @*/
    public static Number natNil() {
        Number ret_1 = null;

        //@ assert ((ret_1 == null) || Utils.is_nat(ret_1));
        return ret_1;
    }

    /*@ pure @*/
    public static Number natOne() {
        Number ret_2 = 1L;

        //@ assert ((ret_2 == null) || Utils.is_nat(ret_2));
        return ret_2;
    }

    public String toString() {
        return "Entry{}";
    }
}
