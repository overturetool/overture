package project.Entrytypes;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class A implements Record {
    public VDMMap m;

    //@ public instance invariant project.Entry.invChecksOn ==> inv_A(m);
    public A(final VDMMap _m) {
        m = (_m != null) ? Utils.copy(_m) : null;
    }

    /*@ pure @*/
    public boolean equals(final Object obj) {
        if (!(obj instanceof project.Entrytypes.A)) {
            return false;
        }

        project.Entrytypes.A other = ((project.Entrytypes.A) obj);

        return Utils.equals(m, other.m);
    }

    /*@ pure @*/
    public int hashCode() {
        return Utils.hashCode(m);
    }

    /*@ pure @*/
    public project.Entrytypes.A copy() {
        return new project.Entrytypes.A(m);
    }

    /*@ pure @*/
    public String toString() {
        return "mk_Entry`A" + Utils.formatFields(m);
    }

    /*@ pure @*/
    public VDMMap get_m() {
        VDMMap ret_3 = m;

        //@ assert ret_3 != null;
        return ret_3;
    }

    public void set_m(final VDMMap _m) {
        //@ assert _m != null;
        m = _m;

        //@ assert m != null;
    }

    /*@ pure @*/
    public Boolean valid() {
        return true;
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_A(final VDMMap _m) {
        Boolean forAllExpResult_2 = true;
        VDMSet set_2 = MapUtil.dom(_m);

        for (Iterator iterator_2 = set_2.iterator();
                iterator_2.hasNext() && forAllExpResult_2;) {
            Number i = ((Number) iterator_2.next());
            forAllExpResult_2 = Utils.equals(((project.Entrytypes.B) Utils.get(
                        _m, i)).x, 2L);
        }

        return forAllExpResult_2;
    }
}
