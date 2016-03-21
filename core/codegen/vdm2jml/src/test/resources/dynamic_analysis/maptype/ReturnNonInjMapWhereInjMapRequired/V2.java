package project.Entrytypes;

import org.overture.codegen.runtime.*;
import org.overture.codegen.vdm2jml.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class V2 implements Record {
    public Number x;
    public Number y;

    public V2(final Number _x, final Number _y) {
        //@ assert Utils.is_int(_x);

        //@ assert Utils.is_int(_y);
        x = _x;
        //@ assert Utils.is_int(x);
        y = _y;

        //@ assert Utils.is_int(y);
    }

    /*@ pure @*/
    public boolean equals(final Object obj) {
        if (!(obj instanceof project.Entrytypes.V2)) {
            return false;
        }

        project.Entrytypes.V2 other = ((project.Entrytypes.V2) obj);

        return (Utils.equals(x, other.x)) && (Utils.equals(y, other.y));
    }

    /*@ pure @*/
    public int hashCode() {
        return Utils.hashCode(x, y);
    }

    /*@ pure @*/
    public project.Entrytypes.V2 copy() {
        return new project.Entrytypes.V2(x, y);
    }

    /*@ pure @*/
    public String toString() {
        return "mk_Entry`V2" + Utils.formatFields(x, y);
    }

    /*@ pure @*/
    public Number get_x() {
        Number ret_3 = x;

        //@ assert project.Entry.invChecksOn ==> (Utils.is_int(ret_3));
        return ret_3;
    }

    public void set_x(final Number _x) {
        //@ assert project.Entry.invChecksOn ==> (Utils.is_int(_x));
        x = _x;

        //@ assert project.Entry.invChecksOn ==> (Utils.is_int(x));
    }

    /*@ pure @*/
    public Number get_y() {
        Number ret_4 = y;

        //@ assert project.Entry.invChecksOn ==> (Utils.is_int(ret_4));
        return ret_4;
    }

    public void set_y(final Number _y) {
        //@ assert project.Entry.invChecksOn ==> (Utils.is_int(_y));
        y = _y;

        //@ assert project.Entry.invChecksOn ==> (Utils.is_int(y));
    }

    /*@ pure @*/
    public Boolean valid() {
        return true;
    }
}
