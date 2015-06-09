package project.Entrytypes;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
public class St implements Record {
    //@ public instance invariant inv_Entry_PT(x) && (x == null || inv_Entry_PossiblyOne(x) || inv_Entry_True(x));
    public Object x;

    public St(final Object _x) {
        //@ assert inv_Entry_PT(_x) && (_x == null || inv_Entry_PossiblyOne(_x) || inv_Entry_True(_x));
        x = (_x != null) ? _x : null;

        //@ assert inv_Entry_PT(x) && (x == null || inv_Entry_PossiblyOne(x) || inv_Entry_True(x));
    }

    /*@ pure @*/
    public boolean equals(final Object obj) {
        if (!(obj instanceof project.Entrytypes.St)) {
            return false;
        }

        project.Entrytypes.St other = ((project.Entrytypes.St) obj);

        return Utils.equals(x, other.x);
    }

    /*@ pure @*/
    public int hashCode() {
        return Utils.hashCode(x);
    }

    /*@ pure @*/
    public project.Entrytypes.St copy() {
        return new project.Entrytypes.St(x);
    }

    /*@ pure @*/
    public String toString() {
        return "mk_Entry`St" + Utils.formatFields(x);
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_PT(final Object check_elem) {
        if (!(Utils.equals(check_elem, null)) &&
                !(Utils.is_nat(check_elem) || Utils.is_bool(check_elem))) {
            return false;
        }

        return true;
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_PossiblyOne(final Object check_p) {
        if (!(Utils.equals(check_p, null)) && !(Utils.is_nat(check_p))) {
            return false;
        }

        Number p = ((Number) check_p);

        Boolean orResult_1 = false;

        if (!(!(Utils.equals(p, null)))) {
            orResult_1 = true;
        } else {
            orResult_1 = Utils.equals(p, 1L);
        }

        return orResult_1;
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_True(final Object check_b) {
        if ((Utils.equals(check_b, null)) || !(Utils.is_bool(check_b))) {
            return false;
        }

        Boolean b = ((Boolean) check_b);

        return b;
    }
}
