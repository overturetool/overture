package project.Entrytypes;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class R3 implements Record {
    public Number x;

    //@ public instance invariant project.Entry.invChecksOn ==> inv_R3(x);
    public R3(final Number _x) {
        x = _x;
    }

    /*@ pure @*/
    public boolean equals(final Object obj) {
        if (!(obj instanceof project.Entrytypes.R3)) {
            return false;
        }

        project.Entrytypes.R3 other = ((project.Entrytypes.R3) obj);

        return Utils.equals(x, other.x);
    }

    /*@ pure @*/
    public int hashCode() {
        return Utils.hashCode(x);
    }

    /*@ pure @*/
    public project.Entrytypes.R3 copy() {
        return new project.Entrytypes.R3(x);
    }

    /*@ pure @*/
    public String toString() {
        return "mk_Entry`R3" + Utils.formatFields(x);
    }

    /*@ pure @*/
    public Number get_x() {
        Number ret_5 = x;

        //@ assert ret_5 != null;
        return ret_5;
    }

    public void set_x(final Number _x) {
        //@ assert _x != null;
        x = _x;

        //@ assert x != null;
    }

    /*@ pure @*/
    public Boolean valid() {
        return true;
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_R3(final Number _x) {
        return !(Utils.equals(_x, 3L));
    }
}
