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
        project.Entrytypes.R1 r1 = new project.Entrytypes.R1(new project.Entrytypes.R2(
                    5L));

        //@ assert Utils.is_(r1,project.Entrytypes.R1.class);
        project.Entrytypes.R2 stateDes_1 = r1.get_r2();
        //@ assert stateDes_1 != null;
        stateDes_1.set_x(-1L);
        //@ assert Utils.is_(r1,project.Entrytypes.R1.class);

        //@ assert \invariant_for(r1);
        IO.println("\\invariant_for is not implemented in OpenJML RAC " +
            "so the \\invariant_for check will not detect the invariant violation");

        return 0L;
    }

    public String toString() {
        return "Entry{}";
    }
}
