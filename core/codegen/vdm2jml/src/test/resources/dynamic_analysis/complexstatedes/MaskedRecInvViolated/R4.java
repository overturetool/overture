package project.Entrytypes;

import org.overture.codegen.runtime.*;
import org.overture.codegen.vdm2jml.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class R4 implements Record {
    public Number x;

    //@ public instance invariant project.Entry.invChecksOn ==> inv_R4(x);
    public R4(final Number _x) {
        //@ assert Utils.is_int(_x);
        x = _x;

        //@ assert Utils.is_int(x);
    }

    /*@ pure @*/
    public boolean equals(final Object obj) {
        if (!(obj instanceof project.Entrytypes.R4)) {
            return false;
        }

        project.Entrytypes.R4 other = ((project.Entrytypes.R4) obj);

        return Utils.equals(x, other.x);
    }

    /*@ pure @*/
    public int hashCode() {
        return Utils.hashCode(x);
    }

    /*@ pure @*/
    public project.Entrytypes.R4 copy() {
        return new project.Entrytypes.R4(x);
    }

    /*@ pure @*/
    public String toString() {
        return "mk_Entry`R4" + Utils.formatFields(x);
    }

    /*@ pure @*/
    public Number get_x() {
        Number ret_6 = x;

        //@ assert project.Entry.invChecksOn ==> (Utils.is_int(ret_6));
        return ret_6;
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
    public static Boolean inv_R4(final Number _x) {
        return !(Utils.equals(_x, 4L));
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_T3(final Object check_t3) {
        project.Entrytypes.R3 t3 = ((project.Entrytypes.R3) check_t3);

        return !(Utils.equals(t3.get_r4().get_x(), 10L));
    }
}
