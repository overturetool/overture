package project.Entrytypes;

import org.overture.codegen.runtime.*;
import org.overture.codegen.vdm2jml.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class R implements Record {
    public Number x;
    public Number y;

    public R(final Number _x, final Number _y) {
        //@ assert Utils.is_nat(_x);

        //@ assert Utils.is_nat(_y);
        x = _x;
        //@ assert Utils.is_nat(x);
        y = _y;

        //@ assert Utils.is_nat(y);
    }

    /*@ pure @*/
    public boolean equals(final Object obj) {
        if (!(obj instanceof project.Entrytypes.R)) {
            return false;
        }

        project.Entrytypes.R other = ((project.Entrytypes.R) obj);

        return (Utils.equals(x, other.x)) && (Utils.equals(y, other.y));
    }

    /*@ pure @*/
    public int hashCode() {
        return Utils.hashCode(x, y);
    }

    /*@ pure @*/
    public project.Entrytypes.R copy() {
        return new project.Entrytypes.R(x, y);
    }

    /*@ pure @*/
    public String toString() {
        return "mk_Entry`R" + Utils.formatFields(x, y);
    }

    /*@ pure @*/
    public Number get_x() {
        Number ret_2 = x;

        //@ assert project.Entry.invChecksOn ==> (Utils.is_nat(ret_2));
        return ret_2;
    }

    public void set_x(final Number _x) {
        //@ assert project.Entry.invChecksOn ==> (Utils.is_nat(_x));
        x = _x;

        //@ assert project.Entry.invChecksOn ==> (Utils.is_nat(x));
    }

    /*@ pure @*/
    public Number get_y() {
        Number ret_3 = y;

        //@ assert project.Entry.invChecksOn ==> (Utils.is_nat(ret_3));
        return ret_3;
    }

    public void set_y(final Number _y) {
        //@ assert project.Entry.invChecksOn ==> (Utils.is_nat(_y));
        y = _y;

        //@ assert project.Entry.invChecksOn ==> (Utils.is_nat(y));
    }

    /*@ pure @*/
    public Boolean valid() {
        return true;
    }
}
