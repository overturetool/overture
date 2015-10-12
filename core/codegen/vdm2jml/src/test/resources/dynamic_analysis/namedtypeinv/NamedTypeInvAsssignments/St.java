package project.Entrytypes;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class St implements Record {
    public Object x;

    public St(final Object _x) {
        //@ assert ((_x == null) || ((_x == null) || ((_x == null) || ((_x == null) || Utils.is_nat(_x)) && inv_Entry_PossiblyOne(_x)) || (Utils.is_bool(_x) && inv_Entry_True(_x))) && inv_Entry_PT(_x));
        x = (_x != null) ? _x : null;

        //@ assert ((x == null) || ((x == null) || ((x == null) || ((x == null) || Utils.is_nat(x)) && inv_Entry_PossiblyOne(x)) || (Utils.is_bool(x) && inv_Entry_True(x))) && inv_Entry_PT(x));
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
    public Object get_x() {
        Object ret_1 = x;

        //@ assert project.Entry.invChecksOn ==> (((ret_1 == null) || ((ret_1 == null) || ((ret_1 == null) || ((ret_1 == null) || Utils.is_nat(ret_1)) && inv_Entry_PossiblyOne(ret_1)) || (Utils.is_bool(ret_1) && inv_Entry_True(ret_1))) && inv_Entry_PT(ret_1)));
        return ret_1;
    }

    public void set_x(final Object _x) {
        //@ assert project.Entry.invChecksOn ==> (((_x == null) || ((_x == null) || ((_x == null) || ((_x == null) || Utils.is_nat(_x)) && inv_Entry_PossiblyOne(_x)) || (Utils.is_bool(_x) && inv_Entry_True(_x))) && inv_Entry_PT(_x)));
        x = _x;

        //@ assert project.Entry.invChecksOn ==> (((x == null) || ((x == null) || ((x == null) || ((x == null) || Utils.is_nat(x)) && inv_Entry_PossiblyOne(x)) || (Utils.is_bool(x) && inv_Entry_True(x))) && inv_Entry_PT(x)));
    }

    /*@ pure @*/
    public Boolean valid() {
        return true;
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_PT(final Object check_elem) {
        return true;
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_PossiblyOne(final Object check_p) {
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
        Boolean b = ((Boolean) check_b);

        return b;
    }
}
