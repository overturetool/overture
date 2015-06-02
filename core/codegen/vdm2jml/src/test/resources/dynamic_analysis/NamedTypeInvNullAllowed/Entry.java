package project;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
public class Entry {
    public static Object Run() {
        Object e = null;

        //@ assert inv_Entry_N(e) && (e == null || inv_Entry_X(e) || inv_Entry_Y(e));
        return ((Object) e);
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
