package project;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
public class Entry {
    //@ public static invariant inv_Entry_CN(fOk) && (inv_Entry_C(fOk) || inv_Entry_N(fOk));
    public static final Object fOk = 'a';

    //@ public static invariant inv_Entry_CN(fBreak) && (inv_Entry_C(fBreak) || inv_Entry_N(fBreak));
    public static final Object fBreak = 'b';

    public static Object Run() {
        return 0L;
    }

    public String toString() {
        return "Entry{" + "fOk = " + Utils.toString(fOk) + ", fBreak = " +
        Utils.toString(fBreak) + "}";
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_CN(final Object check_cn) {
        if ((Utils.equals(check_cn, null)) ||
                !(Utils.is_char(check_cn) || Utils.is_nat(check_cn))) {
            return false;
        }

        Object cn = ((Object) check_cn);

        Boolean orResult_1 = false;

        if (!(Utils.is_char(cn))) {
            orResult_1 = true;
        } else {
            orResult_1 = Utils.equals(cn, 'a');
        }

        return orResult_1;
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_N(final Object check_elem) {
        if ((Utils.equals(check_elem, null)) || !(Utils.is_nat(check_elem))) {
            return false;
        }

        return true;
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_C(final Object check_elem) {
        if ((Utils.equals(check_elem, null)) || !(Utils.is_char(check_elem))) {
            return false;
        }

        return true;
    }
}
