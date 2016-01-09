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
        {
            Number ignorePattern_1 = f();

            //@ assert Utils.is_nat(ignorePattern_1);

            /* skip */
        }

        IO.println("Done! Expected no violations");

        return 0L;
    }

    /*@ pure @*/
    public static Number f() {
        project.Entrytypes.R recordPattern_1 = new project.Entrytypes.R(1L, 2L);

        //@ assert Utils.is_(recordPattern_1,project.Entrytypes.R.class);
        Boolean success_1 = true;

        //@ assert Utils.is_bool(success_1);
        Number a = null;

        Number b = null;

        a = recordPattern_1.get_x();
        //@ assert Utils.is_nat(a);
        b = recordPattern_1.get_y();

        //@ assert Utils.is_nat(b);
        if (!(success_1)) {
            throw new RuntimeException("Record pattern match failed");
        }

        Number ret_1 = a.longValue() + b.longValue();

        //@ assert Utils.is_nat(ret_1);
        return ret_1;
    }

    public String toString() {
        return "Entry{}";
    }
}
