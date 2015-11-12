package project.Entrytypes;

import org.overture.codegen.runtime.*;
import org.overture.codegen.vdm2jml.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class B1 implements Record {
    public project.Entrytypes.B2 f;

    //@ public instance invariant project.Entry.invChecksOn ==> inv_B1(f);
    public B1(final project.Entrytypes.B2 _f) {
        //@ assert Utils.is_(_f,project.Entrytypes.B2.class);
        f = (_f != null) ? Utils.copy(_f) : null;

        //@ assert Utils.is_(f,project.Entrytypes.B2.class);
    }

    /*@ pure @*/
    public boolean equals(final Object obj) {
        if (!(obj instanceof project.Entrytypes.B1)) {
            return false;
        }

        project.Entrytypes.B1 other = ((project.Entrytypes.B1) obj);

        return Utils.equals(f, other.f);
    }

    /*@ pure @*/
    public int hashCode() {
        return Utils.hashCode(f);
    }

    /*@ pure @*/
    public project.Entrytypes.B1 copy() {
        return new project.Entrytypes.B1(f);
    }

    /*@ pure @*/
    public String toString() {
        return "mk_Entry`B1" + Utils.formatFields(f);
    }

    /*@ pure @*/
    public project.Entrytypes.B2 get_f() {
        project.Entrytypes.B2 ret_3 = f;

        //@ assert project.Entry.invChecksOn ==> (Utils.is_(ret_3,project.Entrytypes.B2.class));
        return ret_3;
    }

    public void set_f(final project.Entrytypes.B2 _f) {
        //@ assert project.Entry.invChecksOn ==> (Utils.is_(_f,project.Entrytypes.B2.class));
        f = _f;

        //@ assert project.Entry.invChecksOn ==> (Utils.is_(f,project.Entrytypes.B2.class));
    }

    /*@ pure @*/
    public Boolean valid() {
        return true;
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_B1(final project.Entrytypes.B2 _f) {
        return _f.x.longValue() > 0L;
    }
}
