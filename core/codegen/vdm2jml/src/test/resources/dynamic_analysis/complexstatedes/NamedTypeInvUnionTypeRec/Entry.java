package project;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class Entry {
    /*@ public ghost static boolean invChecksOn = true; @*/
    private Entry() {
    }

    public static Object Run() {
        IO.println("Before useOk");

        {
            Number ignorePattern_1 = useOk();

            /* skip */
        }

        IO.println("After useOk");
        IO.println("Before useNotOk");

        {
            Number ignorePattern_2 = useNotOk();

            /* skip */
        }

        IO.println("After useNotOk");

        return 0L;
    }

    public static Number useOk() {
        project.Entrytypes.R1 r1 = new project.Entrytypes.R1(new project.Entrytypes.R2(
                    new project.Entrytypes.R3(new project.Entrytypes.R4(5L))));
        Number atomicTmp_1 = 10L;

        Number atomicTmp_2 = 3L;

        Number atomicTmp_3 = 5L;

        { /* Start of atomic statement */

            //@ set invChecksOn = false;
            project.Entrytypes.R2 stateDes_1 = r1.get_r2();

            Object stateDes_2 = stateDes_1.get_t3();

            project.Entrytypes.R4 apply_1 = null;

            if (stateDes_2 instanceof project.Entrytypes.R3) {
                apply_1 = ((project.Entrytypes.R3) stateDes_2).get_r4();
            } else {
                throw new RuntimeException("Missing member: r4");
            }

            apply_1.set_x(atomicTmp_1);

            project.Entrytypes.R2 stateDes_3 = r1.get_r2();

            Object stateDes_4 = stateDes_3.get_t3();

            project.Entrytypes.R4 apply_2 = null;

            if (stateDes_4 instanceof project.Entrytypes.R3) {
                apply_2 = ((project.Entrytypes.R3) stateDes_4).get_r4();
            } else {
                throw new RuntimeException("Missing member: r4");
            }

            apply_2.set_x(atomicTmp_2);

            project.Entrytypes.R2 stateDes_5 = r1.get_r2();

            Object stateDes_6 = stateDes_5.get_t3();

            project.Entrytypes.R4 apply_3 = null;

            if (stateDes_6 instanceof project.Entrytypes.R3) {
                apply_3 = ((project.Entrytypes.R3) stateDes_6).get_r4();
            } else {
                throw new RuntimeException("Missing member: r4");
            }

            apply_3.set_x(atomicTmp_3);

            //@ set invChecksOn = true;

            //@ assert apply_1.valid();

            //@ assert stateDes_2 instanceof project.Entrytypes.R3 ==> ((project.Entrytypes.R3) stateDes_2).valid();
            //@ assert stateDes_2 instanceof project.Entrytypes.X ==> ((project.Entrytypes.X) stateDes_2).valid();
            //@ assert inv_Entry_T3(stateDes_2);

            //@ assert stateDes_1.valid();

            //@ assert r1.valid();

            //@ assert apply_2.valid();

            //@ assert stateDes_4 instanceof project.Entrytypes.R3 ==> ((project.Entrytypes.R3) stateDes_4).valid();
            //@ assert stateDes_4 instanceof project.Entrytypes.X ==> ((project.Entrytypes.X) stateDes_4).valid();
            //@ assert inv_Entry_T3(stateDes_4);

            //@ assert stateDes_3.valid();

            //@ assert apply_3.valid();

            //@ assert stateDes_6 instanceof project.Entrytypes.R3 ==> ((project.Entrytypes.R3) stateDes_6).valid();
            //@ assert stateDes_6 instanceof project.Entrytypes.X ==> ((project.Entrytypes.X) stateDes_6).valid();
            //@ assert inv_Entry_T3(stateDes_6);

            //@ assert stateDes_5.valid();
        } /* End of atomic statement */
        return 0L;
    }

    public static Number useNotOk() {
        project.Entrytypes.R1 r1 = new project.Entrytypes.R1(new project.Entrytypes.R2(
                    new project.Entrytypes.R3(new project.Entrytypes.R4(5L))));
        Number atomicTmp_4 = 3L;

        { /* Start of atomic statement */

            //@ set invChecksOn = false;
            project.Entrytypes.R2 stateDes_7 = r1.get_r2();

            Object stateDes_8 = stateDes_7.get_t3();

            project.Entrytypes.R4 apply_4 = null;

            if (stateDes_8 instanceof project.Entrytypes.R3) {
                apply_4 = ((project.Entrytypes.R3) stateDes_8).get_r4();
            } else {
                throw new RuntimeException("Missing member: r4");
            }

            apply_4.set_x(atomicTmp_4);

            //@ set invChecksOn = true;

            //@ assert apply_4.valid();

            //@ assert stateDes_8 instanceof project.Entrytypes.R3 ==> ((project.Entrytypes.R3) stateDes_8).valid();
            //@ assert stateDes_8 instanceof project.Entrytypes.X ==> ((project.Entrytypes.X) stateDes_8).valid();
            //@ assert inv_Entry_T3(stateDes_8);

            //@ assert stateDes_7.valid();

            //@ assert r1.valid();
        } /* End of atomic statement */
        return 0L;
    }

    public String toString() {
        return "Entry{}";
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_T3(final Object check_t3) {
        if ((Utils.equals(check_t3, null)) ||
                !(Utils.is_(check_t3, project.Entrytypes.R3.class) ||
                Utils.is_(check_t3, project.Entrytypes.X.class))) {
            return false;
        }

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
