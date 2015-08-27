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
                    new project.Entrytypes.R3(5L)));
        Number atomicTmp_1 = 1L;

        Number atomicTmp_2 = 5L;

        { /* Start of atomic statement */

            //@ set invChecksOn = false;
            project.Entrytypes.R2 stateDes_1 = r1.get_r2();

            project.Entrytypes.R3 stateDes_2 = stateDes_1.get_r3();

            stateDes_2.set_x(atomicTmp_1);

            project.Entrytypes.R2 stateDes_3 = r1.get_r2();

            project.Entrytypes.R3 stateDes_4 = stateDes_3.get_r3();

            stateDes_4.set_x(atomicTmp_2);

            //@ set invChecksOn = true;

            //@ assert stateDes_2.valid();

            //@ assert stateDes_1.valid();

            //@ assert r1.valid();

            //@ assert stateDes_4.valid();

            //@ assert stateDes_3.valid();
        } /* End of atomic statement */
        return 0L;
    }

    public static Number useNotOk() {
        project.Entrytypes.R1 r1 = new project.Entrytypes.R1(new project.Entrytypes.R2(
                    new project.Entrytypes.R3(5L)));
        Number atomicTmp_3 = 1L;

        { /* Start of atomic statement */

            //@ set invChecksOn = false;
            project.Entrytypes.R2 stateDes_5 = r1.get_r2();

            project.Entrytypes.R3 stateDes_6 = stateDes_5.get_r3();

            stateDes_6.set_x(atomicTmp_3);

            //@ set invChecksOn = true;

            //@ assert stateDes_6.valid();

            //@ assert stateDes_5.valid();

            //@ assert r1.valid();
        } /* End of atomic statement */
        return 0L;
    }

    public String toString() {
        return "Entry{}";
    }
}
