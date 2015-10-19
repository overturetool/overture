package project.Entrytypes;

import org.overture.codegen.runtime.*;
import org.overture.codegen.vdm2jml.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class T1 implements Record {
    public project.Entrytypes.T2 t2;

    //@ public instance invariant project.Entry.invChecksOn ==> inv_T1(t2);
    public T1(final project.Entrytypes.T2 _t2) {
        //@ assert Utils.is_(_t2,project.Entrytypes.T2.class);
        t2 = (_t2 != null) ? Utils.copy(_t2) : null;

        //@ assert Utils.is_(t2,project.Entrytypes.T2.class);
    }

    /*@ pure @*/
    public boolean equals(final Object obj) {
        if (!(obj instanceof project.Entrytypes.T1)) {
            return false;
        }

        project.Entrytypes.T1 other = ((project.Entrytypes.T1) obj);

        return Utils.equals(t2, other.t2);
    }

    /*@ pure @*/
    public int hashCode() {
        return Utils.hashCode(t2);
    }

    /*@ pure @*/
    public project.Entrytypes.T1 copy() {
        return new project.Entrytypes.T1(t2);
    }

    /*@ pure @*/
    public String toString() {
        return "mk_Entry`T1" + Utils.formatFields(t2);
    }

    /*@ pure @*/
    public project.Entrytypes.T2 get_t2() {
        project.Entrytypes.T2 ret_3 = t2;

        //@ assert project.Entry.invChecksOn ==> (Utils.is_(ret_3,project.Entrytypes.T2.class));
        return ret_3;
    }

    public void set_t2(final project.Entrytypes.T2 _t2) {
        //@ assert project.Entry.invChecksOn ==> (Utils.is_(_t2,project.Entrytypes.T2.class));
        t2 = _t2;

        //@ assert project.Entry.invChecksOn ==> (Utils.is_(t2,project.Entrytypes.T2.class));
    }

    /*@ pure @*/
    public Boolean valid() {
        return true;
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_T1(final project.Entrytypes.T2 _t2) {
        return _t2.t3.t4.x.longValue() > 1L;
    }
}
