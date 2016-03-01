package project;

import org.overture.codegen.runtime.*;
import org.overture.codegen.vdm2jml.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class Entry {
    /*@ spec_public @*/
    private static project.Entrytypes.St St = new project.Entrytypes.St(1L);

    /*@ public ghost static boolean invChecksOn = true; @*/
    private Entry() {
    }

    public static Object Run() {
        IO.println("Before atomic");

        Number atomicTmp_1 = 2L;

        //@ assert Utils.is_nat(atomicTmp_1);
        Number atomicTmp_2 = 1L;
        //@ assert Utils.is_nat(atomicTmp_2);
        { /* Start of atomic statement */
            //@ set invChecksOn = false;

            //@ assert St != null;
            St.set_x(atomicTmp_1);

            //@ assert St != null;
            St.set_x(atomicTmp_2);

            //@ set invChecksOn = true;

            //@ assert St.valid();
        } /* End of atomic statement */
        IO.println("After atomic");

        return St.get_x();
    }

    public String toString() {
        return "Entry{" + "St := " + Utils.toString(St) + "}";
    }
}
