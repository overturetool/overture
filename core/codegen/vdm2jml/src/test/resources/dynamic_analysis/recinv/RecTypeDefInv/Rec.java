package project.Entrytypes;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class Rec implements Record {
    public Number x;
    public Number y;

    //@ public instance invariant project.Entry.invChecksOn ==> inv_Rec(x,y);
    public Rec(final Number _x, final Number _y) {
        //@ assert Utils.is_int(_x);

        //@ assert Utils.is_int(_y);
        x = _x;
        //@ assert Utils.is_int(x);
        y = _y;

        //@ assert Utils.is_int(y);
    }

    /*@ pure @*/
    public boolean equals(final Object obj) {
        if (!(obj instanceof project.Entrytypes.Rec)) {
            return false;
        }

        project.Entrytypes.Rec other = ((project.Entrytypes.Rec) obj);

        return (Utils.equals(x, other.x)) && (Utils.equals(y, other.y));
    }

    /*@ pure @*/
    public int hashCode() {
        return Utils.hashCode(x, y);
    }

    /*@ pure @*/
    public project.Entrytypes.Rec copy() {
        return new project.Entrytypes.Rec(x, y);
    }

    /*@ pure @*/
    public String toString() {
        return "mk_Entry`Rec" + Utils.formatFields(x, y);
    }

    /*@ pure @*/
    public Number get_x() {
        Number ret_1 = x;

        //@ assert project.Entry.invChecksOn ==> (Utils.is_int(ret_1));
        return ret_1;
    }

    public void set_x(final Number _x) {
        //@ assert project.Entry.invChecksOn ==> (Utils.is_int(_x));
        x = _x;

        //@ assert project.Entry.invChecksOn ==> (Utils.is_int(x));
    }

    /*@ pure @*/
    public Number get_y() {
        Number ret_2 = y;

        //@ assert project.Entry.invChecksOn ==> (Utils.is_int(ret_2));
        return ret_2;
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

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Rec(final Number _x, final Number _y) {
        Boolean success_2 = true;
        Number x = null;
        Number y = null;
        x = _x;
        y = _y;

        if (!(success_2)) {
            throw new RuntimeException("Record pattern match failed");
        }

        Boolean andResult_2 = false;

        if (x.longValue() > 0L) {
            if (y.longValue() > 0L) {
                andResult_2 = true;
            }
        }

        return andResult_2;
    }
}
