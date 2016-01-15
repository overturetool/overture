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

    public static Number useOk() {
        project.Entrytypes.T1 t1 = new project.Entrytypes.T1(new project.Entrytypes.T2(
                    new project.Entrytypes.T3(new project.Entrytypes.T4(5L))));

        //@ assert Utils.is_(t1,project.Entrytypes.T1.class);
        project.Entrytypes.T2 stateDes_1 = t1.get_t2();

        project.Entrytypes.T3 stateDes_2 = stateDes_1.get_t3();

        project.Entrytypes.T4 stateDes_3 = stateDes_2.get_t4();

        //@ assert stateDes_3 != null;
        stateDes_3.set_x(6L);

        //@ assert Utils.is_(stateDes_2,project.Entrytypes.T3.class);

        //@ assert stateDes_2.valid();

        //@ assert Utils.is_(stateDes_1,project.Entrytypes.T2.class);

        //@ assert stateDes_1.valid();

        //@ assert Utils.is_(t1,project.Entrytypes.T1.class);

        //@ assert t1.valid();
        project.Entrytypes.T2 stateDes_4 = t1.get_t2();

        project.Entrytypes.T3 stateDes_5 = stateDes_4.get_t3();

        project.Entrytypes.T4 stateDes_6 = stateDes_5.get_t4();

        //@ assert stateDes_6 != null;
        stateDes_6.set_x(7L);

        //@ assert Utils.is_(stateDes_5,project.Entrytypes.T3.class);

        //@ assert stateDes_5.valid();

        //@ assert Utils.is_(stateDes_4,project.Entrytypes.T2.class);

        //@ assert stateDes_4.valid();

        //@ assert Utils.is_(t1,project.Entrytypes.T1.class);

        //@ assert t1.valid();
        Number ret_1 = 0L;

        //@ assert Utils.is_nat(ret_1);
        return ret_1;
    }

    public static Number useNotOk() {
        project.Entrytypes.T1 t1 = new project.Entrytypes.T1(new project.Entrytypes.T2(
                    new project.Entrytypes.T3(new project.Entrytypes.T4(5L))));

        //@ assert Utils.is_(t1,project.Entrytypes.T1.class);
        project.Entrytypes.T2 stateDes_7 = t1.get_t2();

        project.Entrytypes.T3 stateDes_8 = stateDes_7.get_t3();

        project.Entrytypes.T4 stateDes_9 = stateDes_8.get_t4();

        //@ assert stateDes_9 != null;
        stateDes_9.set_x(60L);

        //@ assert Utils.is_(stateDes_8,project.Entrytypes.T3.class);

        //@ assert stateDes_8.valid();

        //@ assert Utils.is_(stateDes_7,project.Entrytypes.T2.class);

        //@ assert stateDes_7.valid();

        //@ assert Utils.is_(t1,project.Entrytypes.T1.class);

        //@ assert t1.valid();
        project.Entrytypes.T2 stateDes_10 = t1.get_t2();

        project.Entrytypes.T3 stateDes_11 = stateDes_10.get_t3();

        project.Entrytypes.T4 stateDes_12 = stateDes_11.get_t4();

        //@ assert stateDes_12 != null;
        stateDes_12.set_x(5L);

        //@ assert Utils.is_(stateDes_11,project.Entrytypes.T3.class);

        //@ assert stateDes_11.valid();

        //@ assert Utils.is_(stateDes_10,project.Entrytypes.T2.class);

        //@ assert stateDes_10.valid();

        //@ assert Utils.is_(t1,project.Entrytypes.T1.class);

        //@ assert t1.valid();
        Number ret_2 = 0L;

        //@ assert Utils.is_nat(ret_2);
        return ret_2;
    }

    public static Object Run() {
        IO.println("Before useOk");

        {
            Number ignorePattern_1 = useOk();

            //@ assert Utils.is_nat(ignorePattern_1);

            /* skip */
        }

        IO.println("After useOk");
        IO.println("Before useNotOk");

        {
            Number ignorePattern_2 = useNotOk();

            //@ assert Utils.is_nat(ignorePattern_2);

            /* skip */
        }

        IO.println("After useNotOk");

        return 0L;
    }

    public String toString() {
        return "Entry{}";
    }
}
