package project.Entrytypes;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
public class Rec implements Record {
    public Number x;
    public Number y;

    //@ public instance invariant inv_Rec(x,y);
    public Rec(final Number _x, final Number _y) {
        x = _x;
        y = _y;
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
