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
        IO.println("Before legal use");

        {
            Object a = true;
            //@ assert (Utils.is_bool(a) || Utils.is_char(a) || Utils.is_nat1(a));
            a = 1L;
            //@ assert (Utils.is_bool(a) || Utils.is_char(a) || Utils.is_nat1(a));
            a = 'a';
            //@ assert (Utils.is_bool(a) || Utils.is_char(a) || Utils.is_nat1(a));
            a = true;

            //@ assert (Utils.is_bool(a) || Utils.is_char(a) || Utils.is_nat1(a));
        }

        {
            Object b = true;
            //@ assert (((b == null) || Utils.is_char(b) || Utils.is_nat1(b)) || Utils.is_bool(b));
            b = null;

            //@ assert (((b == null) || Utils.is_char(b) || Utils.is_nat1(b)) || Utils.is_bool(b));
        }

        IO.println("After legal use");
        IO.println("Before illegal use");

        {
            Object a = charNil();

            //@ assert (Utils.is_bool(a) || Utils.is_char(a) || Utils.is_nat1(a));

            /* skip */
        }

        IO.println("After illegal use");

        return 0L;
    }

    /*@ pure @*/
    public static Character charNil() {
        Character ret_1 = null;

        //@ assert ((ret_1 == null) || Utils.is_char(ret_1));
        return ret_1;
    }

    public String toString() {
        return "Entry{}";
    }
}
