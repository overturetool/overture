package project.Entrytypes;

import org.overture.codegen.runtime.*;
import org.overture.codegen.vdm2jml.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class R1 implements Record {
    public project.Entrytypes.R2 r2;

    //@ public instance invariant project.Entry.invChecksOn ==> inv_R1(r2);
    public R1(final project.Entrytypes.R2 _r2) {
        //@ assert Utils.is_(_r2,project.Entrytypes.R2.class);
        r2 = (_r2 != null) ? Utils.copy(_r2) : null;

        //@ assert Utils.is_(r2,project.Entrytypes.R2.class);
    }

    /*@ pure @*/
    public boolean equals(final Object obj) {
        if (!(obj instanceof project.Entrytypes.R1)) {
            return false;
        }

        project.Entrytypes.R1 other = ((project.Entrytypes.R1) obj);

        return Utils.equals(r2, other.r2);
    }

    /*@ pure @*/
    public int hashCode() {
        return Utils.hashCode(r2);
    }

    /*@ pure @*/
    public project.Entrytypes.R1 copy() {
        return new project.Entrytypes.R1(r2);
    }

    /*@ pure @*/
    public String toString() {
        return "mk_Entry`R1" + Utils.formatFields(r2);
    }

    /*@ pure @*/
    public project.Entrytypes.R2 get_r2() {
        project.Entrytypes.R2 ret_3 = r2;

        //@ assert project.Entry.invChecksOn ==> (Utils.is_(ret_3,project.Entrytypes.R2.class));
        return ret_3;
    }

    public void set_r2(final project.Entrytypes.R2 _r2) {
        //@ assert project.Entry.invChecksOn ==> (Utils.is_(_r2,project.Entrytypes.R2.class));
        r2 = _r2;

        //@ assert project.Entry.invChecksOn ==> (Utils.is_(r2,project.Entrytypes.R2.class));
    }

    /*@ pure @*/
    public Boolean valid() {
        return true;
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_R1(final project.Entrytypes.R2 _r2) {
        Object obj_2 = _r2.t3;
        project.Entrytypes.R4 apply_6 = null;

        if (obj_2 instanceof project.Entrytypes.R3) {
            apply_6 = Utils.copy(((project.Entrytypes.R3) obj_2).r4);
        } else {
            throw new RuntimeException("Missing member: r4");
        }

        return !(Utils.equals(apply_6.x, 1L));
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
