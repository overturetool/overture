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
        Object e = null;

        //@ assert ((e == null) || ((e == null) || (Utils.is_nat(e) && inv_Entry_X(e)) || (Utils.is_char(e) && inv_Entry_Y(e))) && inv_Entry_N(e));
        return e;
    }

    public String toString() {
        return "Entry{}";
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_N(final Object check_elem) {
        if (!(Utils.equals(check_elem, null)) &&
                !(Utils.is_nat(check_elem) || Utils.is_char(check_elem))) {
            return false;
        }

        return true;
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_X(final Object check_elem) {
        if ((Utils.equals(check_elem, null)) || !(Utils.is_nat(check_elem))) {
            return false;
        }

        return true;
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_Y(final Object check_elem) {
        if ((Utils.equals(check_elem, null)) || !(Utils.is_char(check_elem))) {
            return false;
        }

        return true;
    }
}
