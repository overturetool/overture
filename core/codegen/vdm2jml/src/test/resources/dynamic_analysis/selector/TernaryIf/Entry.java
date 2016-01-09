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
        {
            Number ignorePattern_1 = f();

            //@ assert Utils.is_nat(ignorePattern_1);

            /* skip */
        }

        {
            Number ignorePattern_2 = g();

            //@ assert Utils.is_nat(ignorePattern_2);

            /* skip */
        }

        IO.println("Done! Expected no violations");

        return 0L;
    }

    /*@ pure @*/
    public static Number g() {
        Number ternaryIfExp_1 = null;

        if (Utils.equals(1L, 1L)) {
            ternaryIfExp_1 = 1L;

            //@ assert Utils.is_nat1(ternaryIfExp_1);
        } else {
            ternaryIfExp_1 = 2L;

            //@ assert Utils.is_nat1(ternaryIfExp_1);
        }

        Number x = ternaryIfExp_1;

        //@ assert Utils.is_nat1(x);
        Number ret_1 = x;

        //@ assert Utils.is_nat(ret_1);
        return ret_1;
    }

    /*@ pure @*/
    public static Number f() {
        if (Utils.equals(1L, 1L)) {
            Number ret_2 = 1L;

            //@ assert Utils.is_nat(ret_2);
            return ret_2;
        } else {
            Number ret_3 = 2L;

            //@ assert Utils.is_nat(ret_3);
            return ret_3;
        }
    }

    public String toString() {
        return "Entry{}";
    }
}
