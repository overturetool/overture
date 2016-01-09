package project;

import org.overture.codegen.runtime.*;
import org.overture.codegen.vdm2jml.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class Entry {
    /*@ public ghost static boolean invChecksOn = true; @*/
    private Entry() {
    }

    public static Object Run() {
        Object r = new project.Entrytypes.A1(new project.Entrytypes.A2(1L));
        //@ assert (Utils.is_(r,project.Entrytypes.A1.class) || Utils.is_(r,project.Entrytypes.B1.class));
        IO.println("Before valid use");

        Object apply_1 = null;

        if (r instanceof project.Entrytypes.A1) {
            apply_1 = ((project.Entrytypes.A1) r).get_f();

            //@ assert (Utils.is_(apply_1,project.Entrytypes.A2.class) || Utils.is_(apply_1,project.Entrytypes.B2.class));
        } else if (r instanceof project.Entrytypes.B1) {
            apply_1 = ((project.Entrytypes.B1) r).get_f();

            //@ assert (Utils.is_(apply_1,project.Entrytypes.A2.class) || Utils.is_(apply_1,project.Entrytypes.B2.class));
        } else {
            throw new RuntimeException("Missing member: f");
        }

        Object stateDes_1 = apply_1;

        if (stateDes_1 instanceof project.Entrytypes.A2) {
            //@ assert stateDes_1 != null;
            ((project.Entrytypes.A2) stateDes_1).set_x(5L);

            //@ assert (Utils.is_(r,project.Entrytypes.A1.class) || Utils.is_(r,project.Entrytypes.B1.class));

            //@ assert r instanceof project.Entrytypes.B1 ==> ((project.Entrytypes.B1) r).valid();

            //@ assert r instanceof project.Entrytypes.A1 ==> ((project.Entrytypes.A1) r).valid();
        } else if (stateDes_1 instanceof project.Entrytypes.B2) {
            //@ assert stateDes_1 != null;
            ((project.Entrytypes.B2) stateDes_1).set_x(5L);

            //@ assert (Utils.is_(r,project.Entrytypes.A1.class) || Utils.is_(r,project.Entrytypes.B1.class));

            //@ assert r instanceof project.Entrytypes.B1 ==> ((project.Entrytypes.B1) r).valid();

            //@ assert r instanceof project.Entrytypes.A1 ==> ((project.Entrytypes.A1) r).valid();
        } else {
            throw new RuntimeException("Missing member: x");
        }

        IO.println("After valid use");
        IO.println("Before illegal use");

        Object apply_2 = null;

        if (r instanceof project.Entrytypes.A1) {
            apply_2 = ((project.Entrytypes.A1) r).get_f();

            //@ assert (Utils.is_(apply_2,project.Entrytypes.A2.class) || Utils.is_(apply_2,project.Entrytypes.B2.class));
        } else if (r instanceof project.Entrytypes.B1) {
            apply_2 = ((project.Entrytypes.B1) r).get_f();

            //@ assert (Utils.is_(apply_2,project.Entrytypes.A2.class) || Utils.is_(apply_2,project.Entrytypes.B2.class));
        } else {
            throw new RuntimeException("Missing member: f");
        }

        Object stateDes_2 = apply_2;

        if (stateDes_2 instanceof project.Entrytypes.A2) {
            //@ assert stateDes_2 != null;
            ((project.Entrytypes.A2) stateDes_2).set_x(-5L);

            //@ assert (Utils.is_(r,project.Entrytypes.A1.class) || Utils.is_(r,project.Entrytypes.B1.class));

            //@ assert r instanceof project.Entrytypes.B1 ==> ((project.Entrytypes.B1) r).valid();

            //@ assert r instanceof project.Entrytypes.A1 ==> ((project.Entrytypes.A1) r).valid();
        } else if (stateDes_2 instanceof project.Entrytypes.B2) {
            //@ assert stateDes_2 != null;
            ((project.Entrytypes.B2) stateDes_2).set_x(-5L);

            //@ assert (Utils.is_(r,project.Entrytypes.A1.class) || Utils.is_(r,project.Entrytypes.B1.class));

            //@ assert r instanceof project.Entrytypes.B1 ==> ((project.Entrytypes.B1) r).valid();

            //@ assert r instanceof project.Entrytypes.A1 ==> ((project.Entrytypes.A1) r).valid();
        } else {
            throw new RuntimeException("Missing member: x");
        }

        IO.println("After illegal use");

        return 0L;
    }

    public String toString() {
        return "Entry{}";
    }
}
