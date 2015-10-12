package project;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class Entry {
    //@ public static invariant (((Utils.is_char(fOk) && inv_Entry_C(fOk)) || (Utils.is_nat(fOk) && inv_Entry_N(fOk))) && inv_Entry_CN(fOk));
    public static final Object fOk = 'a';

    //@ public static invariant (((Utils.is_char(fBreak) && inv_Entry_C(fBreak)) || (Utils.is_nat(fBreak) && inv_Entry_N(fBreak))) && inv_Entry_CN(fBreak));
    public static final Object fBreak = 'b';

    /*@ public ghost static boolean invChecksOn = true; @*/
    private Entry() {
    }

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
        return true;
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_C(final Object check_elem) {
        return true;
    }
}
