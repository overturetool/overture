package project.Entrytypes;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class B implements Record {
    public Number x;

    public B(final Number _x) {
        //@ assert Utils.is_nat(_x);
        x = _x;

        //@ assert Utils.is_nat(x);
    }

    /*@ pure @*/
    public boolean equals(final Object obj) {
        if (!(obj instanceof project.Entrytypes.B)) {
            return false;
        }

        project.Entrytypes.B other = ((project.Entrytypes.B) obj);

        return Utils.equals(x, other.x);
    }

    /*@ pure @*/
    public int hashCode() {
        return Utils.hashCode(x);
    }

    /*@ pure @*/
    public project.Entrytypes.B copy() {
        return new project.Entrytypes.B(x);
    }

    /*@ pure @*/
    public String toString() {
        return "mk_Entry`B" + Utils.formatFields(x);
    }

    /*@ pure @*/
    public Number get_x() {
        Number ret_4 = x;

        //@ assert project.Entry.invChecksOn ==> (Utils.is_nat(ret_4));
        return ret_4;
    }

    public void set_x(final Number _x) {
        //@ assert project.Entry.invChecksOn ==> (Utils.is_nat(_x));
        x = _x;

        //@ assert project.Entry.invChecksOn ==> (Utils.is_nat(x));
    }

    /*@ pure @*/
    public Boolean valid() {
        return true;
    }
}
