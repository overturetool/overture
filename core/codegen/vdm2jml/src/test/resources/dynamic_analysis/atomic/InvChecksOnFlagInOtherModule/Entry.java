package project;

import org.overture.codegen.runtime.*;
import org.overture.codegen.vdm2jml.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class Entry {
    private Entry() {
    }

    public static void Run() {
        project.Entrytypes.E e = new project.Entrytypes.E(1L);

        //@ assert Utils.is_(e,project.Entrytypes.E.class);
        Number atomicTmp_3 = -20L;

        //@ assert Utils.is_int(atomicTmp_3);
        Number atomicTmp_4 = 20L;
        //@ assert Utils.is_int(atomicTmp_4);
        { /* Start of atomic statement */
            //@ set project.Mod.invChecksOn = false;

            //@ assert e != null;
            e.set_x(atomicTmp_3);

            //@ assert e != null;
            e.set_x(atomicTmp_4);

            //@ set project.Mod.invChecksOn = true;

            //@ assert e.valid();
        } /* End of atomic statement */
        IO.println("Done! Expected to exit without any errors");
    }

    public String toString() {
        return "Entry{}";
    }
}
