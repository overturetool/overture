package project.Entrytypes;

import org.overture.codegen.runtime.*;
import org.overture.codegen.vdm2jml.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class A1 implements Record {
    public project.Entrytypes.A2 f;

    //@ public instance invariant project.Entry.invChecksOn ==> inv_A1(f);
    public A1(final project.Entrytypes.A2 _f) {
        //@ assert Utils.is_(_f,project.Entrytypes.A2.class);
        f = (_f != null) ? Utils.copy(_f) : null;

        //@ assert Utils.is_(f,project.Entrytypes.A2.class);
    }

    /*@ pure @*/
    public boolean equals(final Object obj) {
        if (!(obj instanceof project.Entrytypes.A1)) {
            return false;
        }

        project.Entrytypes.A1 other = ((project.Entrytypes.A1) obj);

        return Utils.equals(f, other.f);
    }

    /*@ pure @*/
    public int hashCode() {
        return Utils.hashCode(f);
    }

    /*@ pure @*/
    public project.Entrytypes.A1 copy() {
        return new project.Entrytypes.A1(f);
    }

    /*@ pure @*/
    public String toString() {
        return "mk_Entry`A1" + Utils.formatFields(f);
    }

    /*@ pure @*/
    public project.Entrytypes.A2 get_f() {
        project.Entrytypes.A2 ret_1 = f;

        //@ assert project.Entry.invChecksOn ==> (Utils.is_(ret_1,project.Entrytypes.A2.class));
        return ret_1;
    }

    public void set_f(final project.Entrytypes.A2 _f) {
        //@ assert project.Entry.invChecksOn ==> (Utils.is_(_f,project.Entrytypes.A2.class));
        f = _f;

        //@ assert project.Entry.invChecksOn ==> (Utils.is_(f,project.Entrytypes.A2.class));
    }

    /*@ pure @*/
    public Boolean valid() {
        return true;
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_A1(final project.Entrytypes.A2 _f) {
        return _f.x.longValue() > 0L;
    }
}
