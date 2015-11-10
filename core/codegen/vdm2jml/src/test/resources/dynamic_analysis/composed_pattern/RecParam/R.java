package project.Entrytypes;

import org.overture.codegen.runtime.*;
import org.overture.codegen.vdm2jml.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class R implements Record {
    public Boolean b;

    public R(final Boolean _b) {
        //@ assert Utils.is_bool(_b);
        b = _b;

        //@ assert Utils.is_bool(b);
    }

    /*@ pure @*/
    public boolean equals(final Object obj) {
        if (!(obj instanceof project.Entrytypes.R)) {
            return false;
        }

        project.Entrytypes.R other = ((project.Entrytypes.R) obj);

        return Utils.equals(b, other.b);
    }

    /*@ pure @*/
    public int hashCode() {
        return Utils.hashCode(b);
    }

    /*@ pure @*/
    public project.Entrytypes.R copy() {
        return new project.Entrytypes.R(b);
    }

    /*@ pure @*/
    public String toString() {
        return "mk_Entry`R" + Utils.formatFields(b);
    }

    /*@ pure @*/
    public Boolean get_b() {
        Boolean ret_2 = b;

        //@ assert project.Entry.invChecksOn ==> (Utils.is_bool(ret_2));
        return ret_2;
    }

    public void set_b(final Boolean _b) {
        //@ assert project.Entry.invChecksOn ==> (Utils.is_bool(_b));
        b = _b;

        //@ assert project.Entry.invChecksOn ==> (Utils.is_bool(b));
    }

    /*@ pure @*/
    public Boolean valid() {
        return true;
    }
}
