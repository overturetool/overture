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

    public static Number useOk() {
        project.Entrytypes.R1 r1 = new project.Entrytypes.R1(new project.Entrytypes.R2(
                    new project.Entrytypes.R3(5L)));

        //@ assert Utils.is_(r1,project.Entrytypes.R1.class);
        Number atomicTmp_1 = 1L;

        //@ assert Utils.is_int(atomicTmp_1);
        Number atomicTmp_2 = 5L;
        //@ assert Utils.is_int(atomicTmp_2);
        { /* Start of atomic statement */

            //@ set invChecksOn = false;
            project.Entrytypes.R2 stateDes_1 = r1.get_r2();

            project.Entrytypes.R3 stateDes_2 = stateDes_1.get_r3();

            //@ assert stateDes_2 != null;
            stateDes_2.set_x(atomicTmp_1);

            project.Entrytypes.R2 stateDes_3 = r1.get_r2();

            project.Entrytypes.R3 stateDes_4 = stateDes_3.get_r3();

            //@ assert stateDes_4 != null;
            stateDes_4.set_x(atomicTmp_2);

            //@ set invChecksOn = true;

            //@ assert stateDes_2.valid();

            //@ assert Utils.is_(stateDes_1,project.Entrytypes.R2.class);

            //@ assert stateDes_1.valid();

            //@ assert Utils.is_(r1,project.Entrytypes.R1.class);

            //@ assert r1.valid();

            //@ assert stateDes_4.valid();

            //@ assert Utils.is_(stateDes_3,project.Entrytypes.R2.class);

            //@ assert stateDes_3.valid();
        } /* End of atomic statement */
        Number ret_1 = 0L;

        //@ assert Utils.is_nat(ret_1);
        return ret_1;
    }

    public static Number useNotOk() {
        project.Entrytypes.R1 r1 = new project.Entrytypes.R1(new project.Entrytypes.R2(
                    new project.Entrytypes.R3(5L)));

        //@ assert Utils.is_(r1,project.Entrytypes.R1.class);
        Number atomicTmp_3 = 1L;
        //@ assert Utils.is_int(atomicTmp_3);
        { /* Start of atomic statement */

            //@ set invChecksOn = false;
            project.Entrytypes.R2 stateDes_5 = r1.get_r2();

            project.Entrytypes.R3 stateDes_6 = stateDes_5.get_r3();

            //@ assert stateDes_6 != null;
            stateDes_6.set_x(atomicTmp_3);

            //@ set invChecksOn = true;

            //@ assert stateDes_6.valid();

            //@ assert Utils.is_(stateDes_5,project.Entrytypes.R2.class);

            //@ assert stateDes_5.valid();

            //@ assert Utils.is_(r1,project.Entrytypes.R1.class);

            //@ assert r1.valid();
        } /* End of atomic statement */
        Number ret_2 = 0L;

        //@ assert Utils.is_nat(ret_2);
        return ret_2;
    }

    public String toString() {
        return "Entry{}";
    }
}
