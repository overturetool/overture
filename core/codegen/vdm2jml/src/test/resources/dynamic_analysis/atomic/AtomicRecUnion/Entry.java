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
        Object r = new project.Entrytypes.R1(1L);
        //@ assert (Utils.is_(r,project.Entrytypes.R1.class) || Utils.is_(r,project.Entrytypes.R2.class));
        IO.println("Before valid use");

        Number atomicTmp_1 = -5L;

        //@ assert Utils.is_int(atomicTmp_1);
        Number atomicTmp_2 = 5L;
        //@ assert Utils.is_int(atomicTmp_2);
        { /* Start of atomic statement */

            //@ set invChecksOn = false;
            if (r instanceof project.Entrytypes.R1) {
                //@ assert r != null;
                ((project.Entrytypes.R1) r).set_x(atomicTmp_1);
            } else if (r instanceof project.Entrytypes.R2) {
                //@ assert r != null;
                ((project.Entrytypes.R2) r).set_x(atomicTmp_1);
            } else {
                throw new RuntimeException("Missing member: x");
            }

            if (r instanceof project.Entrytypes.R1) {
                //@ assert r != null;
                ((project.Entrytypes.R1) r).set_x(atomicTmp_2);
            } else if (r instanceof project.Entrytypes.R2) {
                //@ assert r != null;
                ((project.Entrytypes.R2) r).set_x(atomicTmp_2);
            } else {
                throw new RuntimeException("Missing member: x");
            }

            //@ set invChecksOn = true;

            //@ assert r instanceof project.Entrytypes.R1 ==> ((project.Entrytypes.R1) r).valid();

            //@ assert r instanceof project.Entrytypes.R2 ==> ((project.Entrytypes.R2) r).valid();
        } /* End of atomic statement */
        IO.println("After valid use");
        IO.println("Before illegal use");

        Number atomicTmp_3 = -5L;
        //@ assert Utils.is_int(atomicTmp_3);
        { /* Start of atomic statement */

            //@ set invChecksOn = false;
            if (r instanceof project.Entrytypes.R1) {
                //@ assert r != null;
                ((project.Entrytypes.R1) r).set_x(atomicTmp_3);
            } else if (r instanceof project.Entrytypes.R2) {
                //@ assert r != null;
                ((project.Entrytypes.R2) r).set_x(atomicTmp_3);
            } else {
                throw new RuntimeException("Missing member: x");
            }

            //@ set invChecksOn = true;

            //@ assert r instanceof project.Entrytypes.R1 ==> ((project.Entrytypes.R1) r).valid();

            //@ assert r instanceof project.Entrytypes.R2 ==> ((project.Entrytypes.R2) r).valid();
        } /* End of atomic statement */
        IO.println("After illegal use");

        return 0L;
    }

    public String toString() {
        return "Entry{}";
    }
}
