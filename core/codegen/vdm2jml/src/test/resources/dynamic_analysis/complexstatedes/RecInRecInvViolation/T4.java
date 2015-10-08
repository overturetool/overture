package project.Entrytypes;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class T4 implements Record {
    public Number x;

    //@ public instance invariant project.Entry.invChecksOn ==> inv_T4(x);
    public T4(final Number _x) {
        //@ assert Utils.is_nat(_x);
        x = _x;

        //@ assert Utils.is_nat(x);
    }

    /*@ pure @*/
    public boolean equals(final Object obj) {
        if (!(obj instanceof project.Entrytypes.T4)) {
            return false;
        }

        project.Entrytypes.T4 other = ((project.Entrytypes.T4) obj);

        return Utils.equals(x, other.x);
    }

    /*@ pure @*/
    public int hashCode() {
        return Utils.hashCode(x);
    }

    /*@ pure @*/
    public project.Entrytypes.T4 copy() {
        return new project.Entrytypes.T4(x);
    }

    /*@ pure @*/
    public String toString() {
        return "mk_Entry`T4" + Utils.formatFields(x);
    }

    /*@ pure @*/
    public Number get_x() {
        Number ret_6 = x;

        //@ assert project.Entry.invChecksOn ==> (Utils.is_nat(ret_6));
        return ret_6;
    }

    public void set_x(final Number _x) {
        //@ assert project.Entry.invChecksOn ==> (Utils.is_nat(_x));
        x = _x;

        //@ assert project.Entry.invChecksOn ==> (Utils.is_nat(x));
    }

    /*@ pure @*/
    public Boolean valid() {
        return true;
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_T4(final Number _x) {
        return _x.longValue() > 4L;
    }
}
