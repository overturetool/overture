package project.Entrytypes;

import org.overture.codegen.runtime.*;
import org.overture.codegen.vdm2jml.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class A2 implements Record {
    public Number x;

    //@ public instance invariant project.Entry.invChecksOn ==> inv_A2(x);
    public A2(final Number _x) {
        //@ assert Utils.is_int(_x);
        x = _x;

        //@ assert Utils.is_int(x);
    }

    /*@ pure @*/
    public boolean equals(final Object obj) {
        if (!(obj instanceof project.Entrytypes.A2)) {
            return false;
        }

        project.Entrytypes.A2 other = ((project.Entrytypes.A2) obj);

        return Utils.equals(x, other.x);
    }

    /*@ pure @*/
    public int hashCode() {
        return Utils.hashCode(x);
    }

    /*@ pure @*/
    public project.Entrytypes.A2 copy() {
        return new project.Entrytypes.A2(x);
    }

    /*@ pure @*/
    public String toString() {
        return "mk_Entry`A2" + Utils.formatFields(x);
    }

    /*@ pure @*/
    public Number get_x() {
        Number ret_2 = x;

        //@ assert project.Entry.invChecksOn ==> (Utils.is_int(ret_2));
        return ret_2;
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
    public static Boolean inv_A2(final Number _x) {
        return _x.longValue() > 0L;
    }
}
