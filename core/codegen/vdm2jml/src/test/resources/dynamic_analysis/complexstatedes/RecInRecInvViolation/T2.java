package project.Entrytypes;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class T2 implements Record {
    public project.Entrytypes.T3 t3;

    //@ public instance invariant project.Entry.invChecksOn ==> inv_T2(t3);
    public T2(final project.Entrytypes.T3 _t3) {
        //@ assert Utils.is_(_t3,project.Entrytypes.T3.class);
        t3 = (_t3 != null) ? Utils.copy(_t3) : null;

        //@ assert Utils.is_(t3,project.Entrytypes.T3.class);
    }

    /*@ pure @*/
    public boolean equals(final Object obj) {
        if (!(obj instanceof project.Entrytypes.T2)) {
            return false;
        }

        project.Entrytypes.T2 other = ((project.Entrytypes.T2) obj);

        return Utils.equals(t3, other.t3);
    }

    /*@ pure @*/
    public int hashCode() {
        return Utils.hashCode(t3);
    }

    /*@ pure @*/
    public project.Entrytypes.T2 copy() {
        return new project.Entrytypes.T2(t3);
    }

    /*@ pure @*/
    public String toString() {
        return "mk_Entry`T2" + Utils.formatFields(t3);
    }

    /*@ pure @*/
    public project.Entrytypes.T3 get_t3() {
        project.Entrytypes.T3 ret_4 = t3;

        //@ assert project.Entry.invChecksOn ==> (Utils.is_(ret_4,project.Entrytypes.T3.class));
        return ret_4;
    }

    public void set_t3(final project.Entrytypes.T3 _t3) {
        //@ assert project.Entry.invChecksOn ==> (Utils.is_(_t3,project.Entrytypes.T3.class));
        t3 = _t3;

        //@ assert project.Entry.invChecksOn ==> (Utils.is_(t3,project.Entrytypes.T3.class));
    }

    /*@ pure @*/
    public Boolean valid() {
        return true;
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_T2(final project.Entrytypes.T3 _t3) {
        Boolean andResult_2 = false;

        if (_t3.t4.x.longValue() > 2L) {
            if (!(Utils.equals(_t3.t4.x, 60L))) {
                andResult_2 = true;
            }
        }

        return andResult_2;
    }
}
