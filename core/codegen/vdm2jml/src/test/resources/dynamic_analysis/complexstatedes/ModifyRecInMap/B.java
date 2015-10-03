package project.Entrytypes;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class B implements Record {
    public Number x;

    public B(final Number _x) {
        x = _x;
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

        //@ assert ret_4 != null;
        return ret_4;
    }

    public void set_x(final Number _x) {
        //@ assert _x != null;
        x = _x;

        //@ assert x != null;
    }

    /*@ pure @*/
    public Boolean valid() {
        return true;
    }
}
