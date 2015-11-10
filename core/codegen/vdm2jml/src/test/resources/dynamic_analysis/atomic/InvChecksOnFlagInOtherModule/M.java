package project.Modtypes;

import org.overture.codegen.runtime.*;
import org.overture.codegen.vdm2jml.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class M implements Record {
    public Number x;

    //@ public instance invariant project.Mod.invChecksOn ==> inv_M(x);
    public M(final Number _x) {
        //@ assert Utils.is_int(_x);
        x = _x;

        //@ assert Utils.is_int(x);
    }

    /*@ pure @*/
    public boolean equals(final Object obj) {
        if (!(obj instanceof project.Modtypes.M)) {
            return false;
        }

        project.Modtypes.M other = ((project.Modtypes.M) obj);

        return Utils.equals(x, other.x);
    }

    /*@ pure @*/
    public int hashCode() {
        return Utils.hashCode(x);
    }

    /*@ pure @*/
    public project.Modtypes.M copy() {
        return new project.Modtypes.M(x);
    }

    /*@ pure @*/
    public String toString() {
        return "mk_Mod`M" + Utils.formatFields(x);
    }

    /*@ pure @*/
    public Number get_x() {
        Number ret_1 = x;

        //@ assert project.Mod.invChecksOn ==> (Utils.is_int(ret_1));
        return ret_1;
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
    public static Boolean inv_M(final Number _x) {
        return _x.longValue() > 0L;
    }
}
