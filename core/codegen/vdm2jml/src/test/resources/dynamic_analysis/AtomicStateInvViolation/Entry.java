package project;

import org.overture.codegen.runtime.*;
import org.overture.codegen.vdm2jml.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class Entry {
    /*@ spec_public @*/
    private static project.Entrytypes.St St = new project.Entrytypes.St(1L);

    //@ public static invariant St != null ==> inv_St(St);
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
        //@ assert inv_St(St);
        return 2L;
    }

    public String toString() {
        return "Entry{" + "St := " + Utils.toString(St) + "}";
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_St(final project.Entrytypes.St s) {
        return Utils.equals(s.x, 1L);
    }
}
