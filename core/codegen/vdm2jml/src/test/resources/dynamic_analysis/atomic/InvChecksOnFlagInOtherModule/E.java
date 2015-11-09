package project.Entrytypes;

import org.overture.codegen.runtime.*;
import org.overture.codegen.vdm2jml.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class E implements Record {
    public Number x;

    //@ public instance invariant project.Mod.invChecksOn ==> inv_E(x);
    public E(final Number _x) {
        //@ assert Utils.is_int(_x);
        x = _x;

        //@ assert Utils.is_int(x);
    }

    /*@ pure @*/
    public boolean equals(final Object obj) {
        if (!(obj instanceof project.Entrytypes.E)) {
            return false;
        }

        project.Entrytypes.E other = ((project.Entrytypes.E) obj);

        return Utils.equals(x, other.x);
    }

    /*@ pure @*/
    public int hashCode() {
        return Utils.hashCode(x);
    }

    /*@ pure @*/
    public project.Entrytypes.E copy() {
        return new project.Entrytypes.E(x);
    }

    /*@ pure @*/
    public String toString() {
        return "mk_Entry`E" + Utils.formatFields(x);
    }

    /*@ pure @*/
    public Number get_x() {
        Number ret_2 = x;

        //@ assert project.Mod.invChecksOn ==> (Utils.is_int(ret_2));
        return ret_2;
    }

    public void set_x(final Number _x) {
        //@ assert project.Mod.invChecksOn ==> (Utils.is_int(_x));
        x = _x;

        //@ assert project.Mod.invChecksOn ==> (Utils.is_int(x));
    }

    /*@ pure @*/
    public Boolean valid() {
        return true;
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_E(final Number _x) {
        return _x.longValue() > 0L;
    }
}
