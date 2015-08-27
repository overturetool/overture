package project.Entrytypes;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class R2 implements Record {
    public project.Entrytypes.R3 r3;

    //@ public instance invariant project.Entry.invChecksOn ==> inv_R2(r3);
    public R2(final project.Entrytypes.R3 _r3) {
        r3 = (_r3 != null) ? Utils.copy(_r3) : null;
    }

    /*@ pure @*/
    public boolean equals(final Object obj) {
        if (!(obj instanceof project.Entrytypes.R2)) {
            return false;
        }

        project.Entrytypes.R2 other = ((project.Entrytypes.R2) obj);

        return Utils.equals(r3, other.r3);
    }

    /*@ pure @*/
    public int hashCode() {
        return Utils.hashCode(r3);
    }

    /*@ pure @*/
    public project.Entrytypes.R2 copy() {
        return new project.Entrytypes.R2(r3);
    }

    /*@ pure @*/
    public String toString() {
        return "mk_Entry`R2" + Utils.formatFields(r3);
    }

    /*@ pure @*/
    public project.Entrytypes.R3 get_r3() {
        return r3;
    }

    public void set_r3(final project.Entrytypes.R3 _r3) {
        r3 = _r3;
    }

    /*@ pure @*/
    public Boolean valid() {
        return true;
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_R2(final project.Entrytypes.R3 _r3) {
        return !(Utils.equals(_r3.x, 2L));
    }
}
