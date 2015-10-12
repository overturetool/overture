package project.Entrytypes;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class X implements Record {
    public Boolean b;

    public X(final Boolean _b) {
        //@ assert Utils.is_bool(_b);
        b = _b;

        //@ assert Utils.is_bool(b);
    }

    /*@ pure @*/
    public boolean equals(final Object obj) {
        if (!(obj instanceof project.Entrytypes.X)) {
            return false;
        }

        project.Entrytypes.X other = ((project.Entrytypes.X) obj);

        return Utils.equals(b, other.b);
    }

    /*@ pure @*/
    public int hashCode() {
        return Utils.hashCode(b);
    }

    /*@ pure @*/
    public project.Entrytypes.X copy() {
        return new project.Entrytypes.X(b);
    }

    /*@ pure @*/
    public String toString() {
        return "mk_Entry`X" + Utils.formatFields(b);
    }

    /*@ pure @*/
    public Boolean get_b() {
        Boolean ret_7 = b;

        //@ assert project.Entry.invChecksOn ==> (Utils.is_bool(ret_7));
        return ret_7;
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

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_T3(final Object check_t3) {
        Object t3 = ((Object) check_t3);

        Boolean andResult_1 = false;

        Boolean orResult_1 = false;

        if (!(Utils.is_(t3, project.Entrytypes.R3.class))) {
            orResult_1 = true;
        } else {
            project.Entrytypes.R4 apply_9 = null;

            if (t3 instanceof project.Entrytypes.R3) {
                apply_9 = ((project.Entrytypes.R3) t3).get_r4();
            } else {
                throw new RuntimeException("Missing member: r4");
            }

            orResult_1 = !(Utils.equals(apply_9.get_x(), 10L));
        }

        if (orResult_1) {
            Boolean orResult_2 = false;

            if (!(Utils.is_(t3, project.Entrytypes.X.class))) {
                orResult_2 = true;
            } else {
                Boolean apply_10 = null;

                if (t3 instanceof project.Entrytypes.X) {
                    apply_10 = ((project.Entrytypes.X) t3).get_b();
                } else {
                    throw new RuntimeException("Missing member: b");
                }

                orResult_2 = apply_10;
            }

            if (orResult_2) {
                andResult_1 = true;
            }
        }

        return andResult_1;
    }
}
