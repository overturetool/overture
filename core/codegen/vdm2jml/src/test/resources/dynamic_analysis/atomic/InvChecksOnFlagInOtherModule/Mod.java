package project;

import org.overture.codegen.runtime.*;
import org.overture.codegen.vdm2jml.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class Mod {
    /*@ public ghost static boolean invChecksOn = true; @*/
    private Mod() {
    }

    public static void op() {
        project.Modtypes.M m = new project.Modtypes.M(1L);

        //@ assert Utils.is_(m,project.Modtypes.M.class);
        Number atomicTmp_1 = -20L;

        //@ assert Utils.is_int(atomicTmp_1);
        Number atomicTmp_2 = 20L;
        //@ assert Utils.is_int(atomicTmp_2);
        { /* Start of atomic statement */
            //@ set invChecksOn = false;

            //@ assert m != null;
            m.set_x(atomicTmp_1);

            //@ assert m != null;
            m.set_x(atomicTmp_2);

            //@ set invChecksOn = true;

            //@ assert m.valid();
        } /* End of atomic statement */}

    public String toString() {
        return "Mod{}";
    }
}
