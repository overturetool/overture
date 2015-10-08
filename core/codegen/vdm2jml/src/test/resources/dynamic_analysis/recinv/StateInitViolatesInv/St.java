package project.Entrytypes;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class St implements Record {
    public Number x;

    //@ public instance invariant project.Entry.invChecksOn ==> inv_St(x);
    public St(final Number _x) {
        //@ assert Utils.is_int(_x);
        x = _x;

        //@ assert Utils.is_int(x);
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
    public Number get_x() {
        Number ret_1 = x;

        //@ assert project.Entry.invChecksOn ==> (Utils.is_int(ret_1));
        return ret_1;
    }

    public void set_x(final Number _x) {
        //@ assert project.Entry.invChecksOn ==> (Utils.is_int(_x));
        x = _x;

        //@ assert project.Entry.invChecksOn ==> (Utils.is_int(x));
    }

    /*@ pure @*/
    public Boolean valid() {
        return true;
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_St(final Number _x) {
        return _x.longValue() > 0L;
    }
}
