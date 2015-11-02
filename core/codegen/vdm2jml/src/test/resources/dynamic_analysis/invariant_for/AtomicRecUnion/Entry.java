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
        Object r1 = new project.Entrytypes.R1(new project.Entrytypes.R2(5L));

        //@ assert (Utils.is_(r1,project.Entrytypes.R1.class) || Utils.is_nat(r1));
        Number atomicTmp_1 = -1L;

        //@ assert Utils.is_int(atomicTmp_1);
        Number atomicTmp_2 = 1L;
        //@ assert Utils.is_int(atomicTmp_2);
        { /* Start of atomic statement */

            //@ set invChecksOn = false;
            project.Entrytypes.R2 apply_1 = null;

            if (r1 instanceof project.Entrytypes.R1) {
                apply_1 = ((project.Entrytypes.R1) r1).get_r2();
            } else {
                throw new RuntimeException("Missing member: r2");
            }

            project.Entrytypes.R2 stateDes_1 = apply_1;
            //@ assert stateDes_1 != null;
            stateDes_1.set_x(atomicTmp_1);

            project.Entrytypes.R2 apply_2 = null;

            if (r1 instanceof project.Entrytypes.R1) {
                apply_2 = ((project.Entrytypes.R1) r1).get_r2();
            } else {
                throw new RuntimeException("Missing member: r2");
            }

            project.Entrytypes.R2 stateDes_2 = apply_2;
            //@ assert stateDes_2 != null;
            stateDes_2.set_x(atomicTmp_2);

            //@ set invChecksOn = true;

            //@ assert \invariant_for(stateDes_1);

            //@ assert (Utils.is_(r1,project.Entrytypes.R1.class) || Utils.is_nat(r1));

            //@ assert r1 instanceof project.Entrytypes.R1 ==> \invariant_for(((project.Entrytypes.R1) r1));

            //@ assert \invariant_for(stateDes_2);
        } /* End of atomic statement */
        IO.println("\\invariant_for is not implemented in OpenJML RAC " +
            "so the \\invariant_for check will not detect the invariant violation");

        return 0L;
    }

    public String toString() {
        return "Entry{}";
    }
}
