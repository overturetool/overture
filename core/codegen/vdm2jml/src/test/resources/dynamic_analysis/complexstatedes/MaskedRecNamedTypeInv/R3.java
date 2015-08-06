package project.Entrytypes;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class R3 implements Record {
    public project.Entrytypes.R4 r4;

    //@ public instance invariant project.Entry.invChecksOn ==> inv_R3(r4);
    public R3(final project.Entrytypes.R4 _r4) {
        r4 = (_r4 != null) ? Utils.copy(_r4) : null;
    }

    /*@ pure @*/
    public boolean equals(final Object obj) {
        if (!(obj instanceof project.Entrytypes.R3)) {
            return false;
        }

        project.Entrytypes.R3 other = ((project.Entrytypes.R3) obj);

        return Utils.equals(r4, other.r4);
    }

    /*@ pure @*/
    public int hashCode() {
        return Utils.hashCode(r4);
    }

    /*@ pure @*/
    public project.Entrytypes.R3 copy() {
        return new project.Entrytypes.R3(r4);
    }

    /*@ pure @*/
    public String toString() {
        return "mk_Entry`R3" + Utils.formatFields(r4);
    }

    /*@ pure @*/
    public project.Entrytypes.R4 get_r4() {
        return r4;
    }

    public void set_r4(final project.Entrytypes.R4 _r4) {
        r4 = _r4;
    }

    /*@ pure @*/
    public Boolean valid() {
        return true;
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_R3(final project.Entrytypes.R4 _r4) {
        return !(Utils.equals(_r4.x, 3L));
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_T3(final Object check_t3) {
        if ((Utils.equals(check_t3, null)) ||
                !(Utils.is_(check_t3, project.Entrytypes.R3.class))) {
            return false;
        }

        project.Entrytypes.R3 t3 = ((project.Entrytypes.R3) check_t3);

        return !(Utils.equals(t3.get_r4().get_x(), 10L));
    }
}
