package project.Entrytypes;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class R2 implements Record {
    public project.Entrytypes.R3 r3;

    //@ public instance invariant project.Entry.invChecksOn ==> inv_R2(r3);
    public R2(final project.Entrytypes.R3 _r3) {
        //@ assert Utils.is_(_r3,project.Entrytypes.R3.class);
        r3 = (_r3 != null) ? Utils.copy(_r3) : null;

        //@ assert Utils.is_(r3,project.Entrytypes.R3.class);
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
        project.Entrytypes.R3 ret_4 = r3;

        //@ assert project.Entry.invChecksOn ==> (Utils.is_(ret_4,project.Entrytypes.R3.class));
        return ret_4;
    }

    public void set_r3(final project.Entrytypes.R3 _r3) {
        //@ assert project.Entry.invChecksOn ==> (Utils.is_(_r3,project.Entrytypes.R3.class));
        r3 = _r3;

        //@ assert project.Entry.invChecksOn ==> (Utils.is_(r3,project.Entrytypes.R3.class));
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
