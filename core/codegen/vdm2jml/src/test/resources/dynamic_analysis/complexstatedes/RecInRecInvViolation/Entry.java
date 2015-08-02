package project;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class Entry {
    /*@ public ghost static boolean invChecksOn = true; @*/
    private Entry() {
    }

    public static Number useOk() {
        project.Entrytypes.T1 t1 = new project.Entrytypes.T1(new project.Entrytypes.T2(
                    new project.Entrytypes.T3(new project.Entrytypes.T4(5L))));
        project.Entrytypes.T2 stateDes_1 = t1.get_t2();

        project.Entrytypes.T3 stateDes_2 = stateDes_1.get_t3();

        project.Entrytypes.T4 stateDes_3 = stateDes_2.get_t4();

        stateDes_3.set_x(6L);

        //@ assert stateDes_2.valid();

        //@ assert stateDes_1.valid();

        //@ assert t1.valid();
        project.Entrytypes.T2 stateDes_4 = t1.get_t2();

        project.Entrytypes.T3 stateDes_5 = stateDes_4.get_t3();

        project.Entrytypes.T4 stateDes_6 = stateDes_5.get_t4();

        stateDes_6.set_x(7L);

        //@ assert stateDes_5.valid();

        //@ assert stateDes_4.valid();

        //@ assert t1.valid();
        return 0L;
    }

    public static Number useNotOk() {
        project.Entrytypes.T1 t1 = new project.Entrytypes.T1(new project.Entrytypes.T2(
                    new project.Entrytypes.T3(new project.Entrytypes.T4(5L))));
        project.Entrytypes.T2 stateDes_7 = t1.get_t2();

        project.Entrytypes.T3 stateDes_8 = stateDes_7.get_t3();

        project.Entrytypes.T4 stateDes_9 = stateDes_8.get_t4();

        stateDes_9.set_x(60L);

        //@ assert stateDes_8.valid();

        //@ assert stateDes_7.valid();

        //@ assert t1.valid();
        project.Entrytypes.T2 stateDes_10 = t1.get_t2();

        project.Entrytypes.T3 stateDes_11 = stateDes_10.get_t3();

        project.Entrytypes.T4 stateDes_12 = stateDes_11.get_t4();

        stateDes_12.set_x(5L);

        //@ assert stateDes_11.valid();

        //@ assert stateDes_10.valid();

        //@ assert t1.valid();
        return 0L;
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

    public String toString() {
        return "Entry{}";
    }
}
